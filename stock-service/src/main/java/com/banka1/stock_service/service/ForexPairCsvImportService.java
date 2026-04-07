package com.banka1.stock_service.service;

import com.banka1.stock_service.config.ForexPairSeedProperties;
import com.banka1.stock_service.domain.ForexPair;
import com.banka1.stock_service.domain.Liquidity;
import com.banka1.stock_service.dto.ForexPairImportResponse;
import com.banka1.stock_service.repository.ForexPairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Imports FX pair reference data from a CSV file and upserts it into the database.
 *
 * <p>The import is idempotent and keyed by the pair ticker.
 * That means:
 *
 * <ul>
 *     <li>if a ticker does not exist yet, a new FX pair is created</li>
 *     <li>if a ticker already exists and at least one imported value changed, the pair is updated</li>
 *     <li>if a ticker already exists and all imported values are the same, the row is counted as unchanged</li>
 * </ul>
 *
 * <p>The importer intentionally accepts only the current production-oriented
 * {@code forex_pairs_seed.csv} format so startup seeding and tests stay deterministic.
 */
@Service
@RequiredArgsConstructor
public class ForexPairCsvImportService {

    private static final List<String> REQUIRED_HEADERS = List.of(
            "Ticker",
            "Base Currency",
            "Quote Currency",
            "Exchange Rate",
            "Liquidity"
    );

    private final ForexPairRepository forexPairRepository;
    private final ForexPairSeedProperties forexPairSeedProperties;
    private final ResourceLoader resourceLoader;

    /**
     * Imports the configured CSV source from application properties.
     *
     * <p>This is the entry point used by the startup seed flow.
     *
     * @return import summary with created, updated, and unchanged counters
     */
    @Transactional
    public ForexPairImportResponse importFromConfiguredCsv() {
        return importFromLocation(forexPairSeedProperties.csvLocation());
    }

    /**
     * Imports FX pairs from the provided Spring resource location.
     *
     * @param csvLocation Spring resource location, for example {@code classpath:seed/forex_pairs_seed.csv}
     * @return import summary
     */
    @Transactional
    public ForexPairImportResponse importFromLocation(String csvLocation) {
        Resource resource = resourceLoader.getResource(csvLocation);
        return importFromResource(resource, csvLocation);
    }

    /**
     * Imports FX pairs from the provided resource.
     *
     * <p>This method intentionally splits the process into two steps:
     *
     * <ol>
     *     <li>parse and validate CSV rows into an intermediate row model</li>
     *     <li>persist those rows as create/update/unchanged database operations</li>
     * </ol>
     *
     * @param resource CSV resource
     * @param source source label used in the response
     * @return import summary
     */
    @Transactional
    public ForexPairImportResponse importFromResource(Resource resource, String source) {
        List<ForexPairCsvRow> rows = parseCsv(resource, source);
        return persistRows(rows, source);
    }

    /**
     * Persists parsed CSV rows into the database using ticker as the stable business key.
     *
     * @param rows validated parsed rows
     * @param source human-readable source label
     * @return import summary
     */
    private ForexPairImportResponse persistRows(List<ForexPairCsvRow> rows, String source) {
        Collection<String> tickers = rows.stream()
                .map(ForexPairCsvRow::ticker)
                .toList();

        Map<String, ForexPair> existingByTicker = forexPairRepository.findAllByTickerIn(tickers)
                .stream()
                .collect(Collectors.toMap(ForexPair::getTicker, Function.identity()));

        List<ForexPair> entitiesToPersist = new ArrayList<>();
        int createdCount = 0;
        int updatedCount = 0;
        int unchangedCount = 0;

        for (ForexPairCsvRow row : rows) {
            ForexPair existingEntity = existingByTicker.get(row.ticker());
            if (existingEntity == null) {
                ForexPair newEntity = new ForexPair();
                applyRow(newEntity, row);
                entitiesToPersist.add(newEntity);
                createdCount++;
                continue;
            }

            if (applyRowIfChanged(existingEntity, row)) {
                entitiesToPersist.add(existingEntity);
                updatedCount++;
                continue;
            }

            unchangedCount++;
        }

        if (!entitiesToPersist.isEmpty()) {
            forexPairRepository.saveAll(entitiesToPersist);
        }

        return new ForexPairImportResponse(
                source,
                rows.size(),
                createdCount,
                updatedCount,
                unchangedCount
        );
    }

    /**
     * Reads and validates a CSV resource and converts it into intermediate row objects.
     *
     * <p>Validation performed here includes:
     *
     * <ul>
     *     <li>resource existence</li>
     *     <li>non-empty header row</li>
     *     <li>presence of all required FX headers</li>
     *     <li>consistent column count per row</li>
     *     <li>duplicate ticker detection inside the same CSV file</li>
     *     <li>ISO currency-code validation for base and quote currencies</li>
     *     <li>positive decimal parsing for {@code Exchange Rate}</li>
     *     <li>enum validation for {@code Liquidity}</li>
     * </ul>
     *
     * @param resource CSV resource to read
     * @param source human-readable source label used in error messages
     * @return parsed CSV rows ready for persistence
     */
    private List<ForexPairCsvRow> parseCsv(Resource resource, String source) {
        if (!resource.exists()) {
            throw new IllegalStateException("FX pair CSV resource does not exist: " + source);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new IllegalArgumentException("FX pair CSV is empty: " + source);
            }

            List<String> headerValues = parseCsvLine(headerLine, 1, source);
            Map<String, Integer> headerIndexes = indexHeaders(headerValues, source);
            validateHeaders(headerIndexes, source);

            List<ForexPairCsvRow> rows = new ArrayList<>();
            Set<String> tickers = new HashSet<>();
            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                List<String> values = parseCsvLine(line, lineNumber, source);
                if (values.stream().allMatch(String::isBlank)) {
                    continue;
                }

                if (values.size() != headerValues.size()) {
                    throw new IllegalArgumentException(
                            "CSV row " + lineNumber + " in " + source + " has " + values.size()
                                    + " columns, expected " + headerValues.size()
                    );
                }

                ForexPairCsvRow row = mapRow(values, headerIndexes, lineNumber, source);
                if (!tickers.add(row.ticker())) {
                    throw new IllegalArgumentException(
                            "Duplicate FX ticker '" + row.ticker() + "' found in " + source
                                    + " on row " + lineNumber
                    );
                }
                rows.add(row);
            }

            return rows;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read FX pair CSV resource: " + source, exception);
        }
    }

    /**
     * Builds a header-name to column-index map from the first CSV row.
     *
     * @param headers parsed header row values
     * @param source source label used in error messages
     * @return header-index map
     */
    private Map<String, Integer> indexHeaders(List<String> headers, String source) {
        Map<String, Integer> headerIndexes = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            String normalizedHeader = headers.get(i).trim();
            if (headerIndexes.putIfAbsent(normalizedHeader, i) != null) {
                throw new IllegalArgumentException("Duplicate CSV header '" + normalizedHeader + "' in " + source);
            }
        }
        return headerIndexes;
    }

    /**
     * Validates that all required business columns from {@code forex_pairs_seed.csv} exist.
     *
     * @param headerIndexes indexed CSV headers
     * @param source source label used in error messages
     */
    private void validateHeaders(Map<String, Integer> headerIndexes, String source) {
        for (String requiredHeader : REQUIRED_HEADERS) {
            if (!headerIndexes.containsKey(requiredHeader)) {
                throw new IllegalArgumentException(
                        "Missing required CSV header '" + requiredHeader + "' in " + source
                );
            }
        }
    }

    /**
     * Converts one parsed CSV row into the intermediate row record used by the importer.
     *
     * @param values row values
     * @param headerIndexes indexed CSV headers
     * @param lineNumber current CSV row number for error reporting
     * @param source source label used in error messages
     * @return parsed row model
     */
    private ForexPairCsvRow mapRow(
            List<String> values,
            Map<String, Integer> headerIndexes,
            int lineNumber,
            String source
    ) {
        String ticker = requiredValue(values, headerIndexes, "Ticker", lineNumber, source);
        String baseCurrency = parseCurrencyCode(
                requiredValue(values, headerIndexes, "Base Currency", lineNumber, source),
                "Base Currency",
                lineNumber,
                source
        );
        String quoteCurrency = parseCurrencyCode(
                requiredValue(values, headerIndexes, "Quote Currency", lineNumber, source),
                "Quote Currency",
                lineNumber,
                source
        );
        BigDecimal exchangeRate = parseExchangeRate(
                requiredValue(values, headerIndexes, "Exchange Rate", lineNumber, source),
                lineNumber,
                source
        );
        Liquidity liquidity = parseLiquidity(
                requiredValue(values, headerIndexes, "Liquidity", lineNumber, source),
                lineNumber,
                source
        );

        return new ForexPairCsvRow(ticker, baseCurrency, quoteCurrency, exchangeRate, liquidity);
    }

    /**
     * Resolves a required CSV value by exact header name.
     *
     * @param values current row values
     * @param headerIndexes indexed CSV headers
     * @param header exact header name
     * @param lineNumber current row number for error reporting
     * @param source source label used in error messages
     * @return non-blank required value
     */
    private String requiredValue(
            List<String> values,
            Map<String, Integer> headerIndexes,
            String header,
            int lineNumber,
            String source
    ) {
        Integer index = headerIndexes.get(header);
        if (index == null || index >= values.size()) {
            throw new IllegalArgumentException("Missing required CSV header '" + header + "' in " + source);
        }

        String value = values.get(index).trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing value for column '" + header + "' on row " + lineNumber + " in " + source
            );
        }
        return value;
    }

    /**
     * Validates one ISO currency code.
     *
     * @param rawValue raw CSV value
     * @param headerName current column name for error reporting
     * @param lineNumber row number used in error messages
     * @param source source label used in error messages
     * @return normalized uppercase currency code
     */
    private String parseCurrencyCode(String rawValue, String headerName, int lineNumber, String source) {
        String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Invalid " + headerName + " '" + rawValue + "' on row " + lineNumber + " in " + source
                            + ". Expected ISO currency code."
            );
        }
        return normalized;
    }

    /**
     * Parses a required positive exchange rate.
     *
     * @param rawValue raw CSV value
     * @param lineNumber row number used in error messages
     * @param source source label used in error messages
     * @return parsed positive exchange rate
     */
    private BigDecimal parseExchangeRate(String rawValue, int lineNumber, String source) {
        try {
            BigDecimal exchangeRate = new BigDecimal(rawValue);
            if (exchangeRate.signum() <= 0) {
                throw new IllegalArgumentException(
                        "Exchange Rate must be positive on row " + lineNumber + " in " + source
                );
            }
            return exchangeRate;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid exchange rate '" + rawValue + "' on row " + lineNumber + " in " + source,
                    exception
            );
        }
    }

    /**
     * Parses the liquidity enum from the CSV file.
     *
     * @param rawValue raw CSV value
     * @param lineNumber row number used in error messages
     * @param source source label used in error messages
     * @return parsed liquidity value
     */
    private Liquidity parseLiquidity(String rawValue, int lineNumber, String source) {
        try {
            return Liquidity.valueOf(rawValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid liquidity '" + rawValue + "' on row " + lineNumber + " in " + source
                            + ". Supported values are " + List.of(Liquidity.values()),
                    exception
            );
        }
    }

    /**
     * Parses one CSV line while respecting quoted values and escaped quotes.
     *
     * @param line raw CSV line
     * @param lineNumber current CSV row number for error reporting
     * @param source source label used in error messages
     * @return parsed row values
     */
    private List<String> parseCsvLine(String line, int lineNumber, String source) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentCharacter = line.charAt(i);

            if (currentCharacter == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++;
                    continue;
                }

                inQuotes = !inQuotes;
                continue;
            }

            if (currentCharacter == ',' && !inQuotes) {
                values.add(currentValue.toString().trim());
                currentValue.setLength(0);
                continue;
            }

            currentValue.append(currentCharacter);
        }

        if (inQuotes) {
            throw new IllegalArgumentException("Unclosed quoted CSV value on row " + lineNumber + " in " + source);
        }

        values.add(currentValue.toString().trim());
        return values;
    }

    /**
     * Copies parsed row values into a persistent entity.
     *
     * @param entity target entity
     * @param row parsed CSV row
     */
    private void applyRow(ForexPair entity, ForexPairCsvRow row) {
        entity.setTicker(row.ticker());
        entity.setBaseCurrency(row.baseCurrency());
        entity.setQuoteCurrency(row.quoteCurrency());
        entity.setExchangeRate(row.exchangeRate());
        entity.setLiquidity(row.liquidity());
    }

    /**
     * Updates an existing entity only when at least one imported value changed.
     *
     * @param entity existing entity from the database
     * @param row parsed CSV row
     * @return {@code true} if the entity was changed and should be persisted
     */
    private boolean applyRowIfChanged(ForexPair entity, ForexPairCsvRow row) {
        if (matches(entity, row)) {
            return false;
        }

        applyRow(entity, row);
        return true;
    }

    /**
     * Compares all imported business fields between the existing entity and the parsed row.
     *
     * @param entity existing entity from the database
     * @param row parsed CSV row
     * @return {@code true} when all imported fields already match
     */
    private boolean matches(ForexPair entity, ForexPairCsvRow row) {
        return Objects.equals(entity.getTicker(), row.ticker())
                && Objects.equals(entity.getBaseCurrency(), row.baseCurrency())
                && Objects.equals(entity.getQuoteCurrency(), row.quoteCurrency())
                && Objects.equals(entity.getExchangeRate(), row.exchangeRate())
                && Objects.equals(entity.getLiquidity(), row.liquidity());
    }

    /**
     * Intermediate immutable representation of one validated FX pair CSV row.
     */
    private record ForexPairCsvRow(
            String ticker,
            String baseCurrency,
            String quoteCurrency,
            BigDecimal exchangeRate,
            Liquidity liquidity
    ) {
    }
}

package com.banka1.exchangeService.client;

import com.banka1.exchangeService.config.ExchangeRateProperties;
import com.banka1.exchangeService.dto.TwelveDataRateResponse;
import com.banka1.exchangeService.exception.BusinessException;
import com.banka1.exchangeService.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * HTTP client for fetching exchange rate data from the Twelve Data service.
 * Handles communication with the external Twelve Data API and parses responses
 * into internal DTO objects.
 */
@Component
@RequiredArgsConstructor
public class TwelveDataClient {

    /**
     * Specialized HTTP client configured for the Twelve Data base URL.
     */
    private final RestClient twelveDataRestClient;

    /**
     * Configuration properties for accessing the Twelve Data API,
     * including the API key and endpoint configuration.
     */
    private final ExchangeRateProperties exchangeRateProperties;

    /**
     * Fetches the current exchange rate for the requested currency pair.
     *
     * @param fromCurrency source currency code
     * @param toCurrency target currency code
     * @return parsed response from the external Twelve Data service
     * @throws BusinessException if the fetch fails or the response is invalid
     */
    public TwelveDataRateResponse fetchExchangeRate(String fromCurrency, String toCurrency) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = twelveDataRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/exchange_rate")
                            .queryParam("symbol", fromCurrency + "/" + toCurrency)
                            .queryParam("apikey", exchangeRateProperties.twelveDataApiKey())
                            .build())
                    .retrieve()
                    .body(Map.class);

            return parseResponse(body, fromCurrency, toCurrency);
        } catch (RestClientException ex) {
            throw new BusinessException(
                    ErrorCode.EXCHANGE_RATE_FETCH_FAILED,
                    "Failed to connect to Twelve Data service."
            );
        }
    }

    /**
     * Validates and maps the raw JSON response from the Twelve Data endpoint
     * into the internal DTO model {@code TwelveDataRateResponse}.
     *
     * @param body deserialized response from the external service
     * @param fromCurrency expected source currency
     * @param toCurrency expected target currency
     * @return parsed response with rate and snapshot date
     * @throws BusinessException if validation fails
     */
    private TwelveDataRateResponse parseResponse(Map<String, Object> body, String fromCurrency, String toCurrency) {
        if (body == null) {
            throw new BusinessException(
                    ErrorCode.EXCHANGE_RATE_FETCH_FAILED,
                    "Twelve Data returned no response."
            );
        }

        if (body.containsKey("code") || body.containsKey("message") || body.containsKey("status")) {
            String message = String.valueOf(body.getOrDefault("message", "Unknown Twelve Data error."));
            throw new BusinessException(ErrorCode.EXCHANGE_RATE_FETCH_FAILED, message);
        }

        String symbol = readRequiredString(body, "symbol");
        BigDecimal rate = readDecimal(body, "rate");

        String expectedSymbol = fromCurrency + "/" + toCurrency;
        if (!expectedSymbol.equalsIgnoreCase(symbol)) {
            throw new BusinessException(
                    ErrorCode.EXCHANGE_RATE_FETCH_FAILED,
                    "Twelve Data returned unexpected currency pair " + symbol + "."
            );
        }

        LocalDate rateDate = LocalDate.now(ZoneOffset.UTC);

        Object timestampRaw = body.get("timestamp");
        if (timestampRaw != null && !String.valueOf(timestampRaw).isBlank()) {
            try {
                long epochSeconds = Long.parseLong(String.valueOf(timestampRaw));
                rateDate = Instant.ofEpochSecond(epochSeconds)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();
            } catch (NumberFormatException ex) {
                throw new BusinessException(
                        ErrorCode.EXCHANGE_RATE_FETCH_FAILED,
                        "Twelve Data returned invalid timestamp."
                );
            }
        }

        return new TwelveDataRateResponse(
                fromCurrency,
                toCurrency,
                rate,
                rateDate
        );
    }

    /**
     * Reads and parses a decimal field from the Twelve Data response.
     * For example, if fieldName is "rate" and the response contains "rate": "117.42",
     * this method returns BigDecimal("117.42").
     * This centralized parsing prevents code duplication throughout the service.
     *
     * @param node map of response fields
     * @param fieldName name of the required field (e.g., "rate")
     * @return parsed decimal value
     * @throws BusinessException if the field is missing or invalid
     */
    private BigDecimal readDecimal(Map<String, Object> node, String fieldName) {
        Object field = node.get(fieldName);
        if (field == null || String.valueOf(field).isBlank()) {
            throw new BusinessException(
                    ErrorCode.EXCHANGE_RATE_FETCH_FAILED,
                    "Twelve Data response missing field " + fieldName + "."
            );
        }
        try {
            return new BigDecimal(String.valueOf(field));
        } catch (NumberFormatException ex) {
            throw new BusinessException(
                    ErrorCode.EXCHANGE_RATE_FETCH_FAILED,
                    "Twelve Data response contains invalid decimal field " + fieldName + "."
            );
        }
    }

    /**
     * Reads and parses a required string field from the Twelve Data response.
     * Uses the same centralized parsing principle as {@code readDecimal} to avoid duplication.
     *
     * @param node map of response fields
     * @param fieldName name of the required field
     * @return non-empty string value
     * @throws BusinessException if the field is missing or empty
     */
    private String readRequiredString(Map<String, Object> node, String fieldName) {
        Object field = node.get(fieldName);
        if (field == null || String.valueOf(field).isBlank()) {
            throw new BusinessException(
                    ErrorCode.EXCHANGE_RATE_FETCH_FAILED,
                    "Twelve Data response missing field " + fieldName + "."
            );
        }
        return String.valueOf(field);
    }
}

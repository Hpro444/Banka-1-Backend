package com.banka1.exchangeService.domain;

import com.banka1.exchangeService.exception.BusinessException;
import com.banka1.exchangeService.exception.ErrorCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Explicitly supported currencies in the exchange-service domain.
 * This enum serves as a central location for validating input currency codes
 * and defining which foreign currencies are fetched from the external API.
 */
public enum SupportedCurrency {
    /**
     * Serbian Dinar - base currency.
     */
    RSD,
    /**
     * Euro.
     */
    EUR,
    /**
     * Swiss Franc.
     */
    CHF,
    /**
     * United States Dollar.
     */
    USD,
    /**
     * British Pound.
     */
    GBP,
    /**
     * Japanese Yen.
     */
    JPY,
    /**
     * Canadian Dollar.
     */
    CAD,
    /**
     * Australian Dollar.
     */
    AUD;

    /**
     * Parses user or API input into a supported enum value.
     *
     * @param currencyCode three-letter ISO currency code
     * @return supported currency from the domain
     */
    public static SupportedCurrency from(String currencyCode) {
        String supportedValues = Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new BusinessException(
                    ErrorCode.UNSUPPORTED_CURRENCY,
                    "Kod valute je obavezan. Podrzane valute: %s.".formatted(supportedValues)
            );
        }

        try {
            return SupportedCurrency.valueOf(currencyCode.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(
                    ErrorCode.UNSUPPORTED_CURRENCY,
                    "Nepodrzan kod valute '%s'. Podrzane valute: %s."
                            .formatted(currencyCode, supportedValues)
            );
        }
    }

    /**
     * Returns the currencies for which the service performs external daily rate fetches.
     * RSD is excluded because it does not require an external provider rate.
     * Technically, RSD has a synthetic rate of 1:1.
     * Since RSD is the base currency, we do not fetch rates for it externally.
     * Therefore, this method returns only EUR, CHF, USD, GBP, JPY, CAD, AUD.
     *
     * @return list of supported foreign currencies to fetch
     */
    public static List<String> trackedCurrencyCodes() {
        return Arrays.stream(values())
                .filter(currency -> currency != RSD)
                .map(Enum::name)
                .toList();
    }
}

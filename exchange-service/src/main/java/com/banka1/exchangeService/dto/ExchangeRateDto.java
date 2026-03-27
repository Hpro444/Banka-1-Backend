package com.banka1.exchangeService.dto;

import com.banka1.exchangeService.domain.SupportedCurrency;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO for displaying a locally stored exchange rate.
 * This is the DTO returned when a client requests a rate from the system,
 * representing how a stored rate appears in the response.
 *
 * @param currencyCode three-letter ISO currency code (e.g., RSD)
 * @param buyingRate rate at which the bank buys the currency from clients
 * @param sellingRate rate at which the bank sells the currency to clients
 * @param date effective date of the exchange rate
 * @param createdAt timestamp when the record was first stored in the database
 */
public record ExchangeRateDto(
        String currencyCode,
        BigDecimal buyingRate,
        BigDecimal sellingRate,
        LocalDate date,
        Instant createdAt
) {

    /**
     * Creates a synthetic rate record for the base currency since RSD does not require external fetches.
     * RSD always has a 1:1 rate relative to itself.
     *
     * @param date snapshot date
     * @return synthetic DTO with a 1:1 rate
     */
    public static ExchangeRateDto baseCurrency(LocalDate date) {
        return new ExchangeRateDto(
                SupportedCurrency.RSD.name(),
                BigDecimal.ONE,
                BigDecimal.ONE,
                date,
                date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)
        );
    }
}

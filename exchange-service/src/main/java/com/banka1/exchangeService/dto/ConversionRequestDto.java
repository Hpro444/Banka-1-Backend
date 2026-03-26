package com.banka1.exchangeService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Internal DTO that the controller passes to the service layer after retrieving
 * and validating conversion calculation query parameters.
 *
 * @param amount amount to be converted
 * @param fromCurrency source currency code
 * @param toCurrency target currency code
 * @param date optional rate list date; if null, uses the latest snapshot
 */
public record ConversionRequestDto(
        BigDecimal amount,
        String fromCurrency,
        String toCurrency,
        LocalDate date
) {
}

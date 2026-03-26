package com.banka1.exchangeService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO for the currency equivalence calculation endpoint.
 * The structure is tailored to the task specification and represents the public API
 * contract that clients receive as a JSON response.
 *
 * @param fromCurrency source currency code
 * @param toCurrency target currency code
 * @param fromAmount original amount from the request
 * @param toAmount calculated amount in the target currency
 * @param rate effective conversion rate, i.e., the ratio {@code toAmount/fromAmount}
 * @param commission calculated commission in the source currency
 * @param date rate list date used in the calculation
 */
public record ConversionResponseDto(
        String fromCurrency,
        String toCurrency,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        BigDecimal rate,
        BigDecimal commission,
        LocalDate date
) {
}

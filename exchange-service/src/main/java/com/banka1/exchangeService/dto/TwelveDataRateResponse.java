package com.banka1.exchangeService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Internal DTO model (backend only) for maintaining a clean Java object
 * after parsing a response from the Twelve Data provider.
 *
 * The flow is:
 * 1. Send an HTTP request to Twelve Data
 * 2. Receive a JSON response
 * 3. Extract the relevant fields from the JSON
 * 4. Package them into TwelveDataRateResponse
 * 5. The service then works with this object
 *
 * @param fromCurrency source currency from the requested pair
 * @param toCurrency target currency from the requested pair
 * @param rate market rate that the provider returns for the currency pair
 * @param date snapshot date derived from the provider timestamp or UTC fetch time
 */
public record TwelveDataRateResponse(
        String fromCurrency,
        String toCurrency,
        BigDecimal rate,
        LocalDate date
) {
}

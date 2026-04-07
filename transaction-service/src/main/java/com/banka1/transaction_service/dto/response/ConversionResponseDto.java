package com.banka1.transaction_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO odgovora za endpoint kalkulacije ekvivalencije valuta.
 * Vraća detaljne rezultate konverzije uključujući konvertovani iznos, kurs i komisiju.
 *
 * @param fromCurrency izvorna valuta (npr. "RSD")
 * @param toCurrency ciljna valuta (npr. "EUR")
 * @param fromAmount originalni iznos iz zahteva
 * @param toAmount preračunati iznos u ciljnoj valuti
 * @param rate efektivni kurs konverzije (toAmount / fromAmount)
 * @param commission obračunata provizija u izvornoj valuti
 * @param date datum kursne liste koji je korišćen u obračunu
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

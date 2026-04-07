package com.banka1.transaction_service.domain.enums;

/**
 * Enum koji definiše sve podržane valute u sistemu.
 * <p>
 * Podržane valute:
 * <ul>
 *   <li>RSD - Srpski dinar</li>
 *   <li>EUR - Evro</li>
 *   <li>CHF - Švajcarski franak</li>
 *   <li>USD - Američki dolar</li>
 *   <li>GBP - Britanska funta</li>
 *   <li>JPY - Japanski jen</li>
 *   <li>CAD - Kanadski dolar</li>
 *   <li>AUD - Australijski dolar</li>
 * </ul>
 */
public enum CurrencyCode {
    /** Srpski dinar */
    RSD,
    /** Evro */
    EUR,
    /** Švajcarski franak */
    CHF,
    /** Američki dolar */
    USD,
    /** Britanska funta */
    GBP,
    /** Japanski jen */
    JPY,
    /** Kanadski dolar */
    CAD,
    /** Australijski dolar */
    AUD
}

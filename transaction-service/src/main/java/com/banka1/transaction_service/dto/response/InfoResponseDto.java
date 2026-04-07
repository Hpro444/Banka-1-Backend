package com.banka1.transaction_service.dto.response;

import com.banka1.transaction_service.domain.enums.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO sa informacijama o računima za transakciju.
 * Sadrži podatke o valutama, vlasnicima i kontakt informacijama.
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class InfoResponseDto {

    /** Valuta izvorne transakcije */
    private CurrencyCode fromCurrencyCode;

    /** Valuta odredišne transakcije */
    private CurrencyCode toCurrencyCode;

    /** ID vlasnika izvorne transakcije */
    private Long fromVlasnik;

    /** ID vlasnika odredišne transakcije */
    private Long toVlasnik;

    /** Email adresa vlasnika izvorne transakcije */
    private String fromEmail;

    /** Korisničko ime vlasnika izvorne transakcije */
    private String fromUsername;
}

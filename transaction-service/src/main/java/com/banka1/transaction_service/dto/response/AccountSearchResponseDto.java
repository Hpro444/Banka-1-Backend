package com.banka1.transaction_service.dto.response;


import com.banka1.transaction_service.domain.enums.AccountOwnershipType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO odgovora za pretragu računa.
 * Koristi se pri pronalaženju računa i pregleda osnovnih informacija o računu.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountSearchResponseDto {

    /** Broj računa */
    private String brojRacuna;

    /** Ime vlasnika računa */
    private String ime;

    /** Prezime vlasnika računa */
    private String prezime;

    /** Tip vlasništva računa (PERSONAL ili BUSINESS) */
    private AccountOwnershipType accountOwnershipType;

    /** Tip računa - tekući ili devizni */
    private String tekuciIliDevizni;


}

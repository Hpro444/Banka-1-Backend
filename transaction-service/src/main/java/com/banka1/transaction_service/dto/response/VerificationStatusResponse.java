package com.banka1.transaction_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO odgovora sa statusom verifikacijske sesije.
 * Koristi se za proveravanje da li je korisnik uspešno prošao 2FA verifikaciju.
 */
@Getter
@Setter
@NoArgsConstructor
public class VerificationStatusResponse {

    /** ID verifikacijske sesije */
    private Long sessionId;

    /** Status sesije: PENDING, VERIFIED, EXPIRED ili CANCELLED */
    private String status;

    /**
     * Proverava da li je sesija uspešno verifikovana.
     *
     * @return true ako je status "VERIFIED", false u svim ostalim slučajevima
     */
    public boolean isVerified() {
        return "VERIFIED".equals(status);
    }
}

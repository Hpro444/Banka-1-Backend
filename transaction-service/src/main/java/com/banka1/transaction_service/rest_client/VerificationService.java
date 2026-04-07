package com.banka1.transaction_service.rest_client;

import com.banka1.transaction_service.dto.response.VerificationStatusResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * REST klijent za komunikaciju sa Verification Service-om.
 * Obezbeđuje operacije za proveravanje statusa 2FA verifikacije korisnika.
 */
@Service
public class VerificationService {

    /** REST klijent sa JWT autentifikacijom */
    private final RestClient restClient;

    /**
     * Konstruktor koji injektuje REST klijenta za Verification Service.
     *
     * @param restClient konfigurisan REST klijent
     */
    public VerificationService(@Qualifier("verificationClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Preuzima status verifikacijske sesije.
     * <p>
     * Koristi se da se proveri da li je korisnik uspešno prošao 2FA verifikaciju.
     *
     * @param sessionId ID verifikacijske sesije
     * @return status sesije sa informacijom da li je verifikovana
     */
    public VerificationStatusResponse getStatus(Long sessionId) {
        return restClient.get()
                .uri("/{sessionId}/status", sessionId)
                .retrieve()
                .body(VerificationStatusResponse.class);
    }
}

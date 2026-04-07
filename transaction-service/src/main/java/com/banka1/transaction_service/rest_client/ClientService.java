package com.banka1.transaction_service.rest_client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * REST klijent za komunikaciju sa korisnikom/client servisom.
 * Obezbeđuje metode za pronalaženje i upravljanje klijentima.
 */
@Service
public class ClientService {

    /** REST klijent sa JWT autentifikacijom */
    private final RestClient restClient;

    /**
     * Konstruktor koji injektuje REST klijenta za User/Client Service.
     *
     * @param restClient konfigurisan REST klijent
     */
    public ClientService(@Qualifier("userClient") RestClient restClient) {
        this.restClient = restClient;
    }

    // TODO: Dodati metode za pronalaženje korisnika po JMBG-u i ID-u

}

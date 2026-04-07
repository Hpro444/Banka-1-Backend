package com.banka1.transaction_service.rest_client;

import com.banka1.transaction_service.dto.request.PaymentDto;
import com.banka1.transaction_service.dto.response.AccountDetailsResponseDto;
import com.banka1.transaction_service.dto.response.InfoResponseDto;
import com.banka1.transaction_service.dto.response.UpdatedBalanceResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * REST klijent za komunikaciju sa Account Service-om.
 * Obezbeđuje metode za preuzimanje informacija o računima i izvršavanje transfera.
 */
@Service
public class AccountService {

    private final RestClient restClient;

    /**
     * Konstruktor koji injektuje REST klijenta za Account Service.
     *
     * @param restClient konfigurisan REST klijent sa JWT autentifikacijom
     */
    public AccountService(@Qualifier("accountClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Preuzima informacije o dve račune neophodne za transakciju.
     *
     * @param fromBankNumber broj polaznog računa
     * @param toBankNumber broj odredišnog računa
     * @return informacije o oba računa (valute, vlasnici, kontakt podaci)
     */
    public InfoResponseDto getInfo(String fromBankNumber, String toBankNumber) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/accounts/info")
                        .queryParam("fromBankNumber", fromBankNumber)
                        .queryParam("toBankNumber", toBankNumber)
                        .build())
                .retrieve()
                .body(InfoResponseDto.class);
    }

    /**
     * Izvršava transfer novca između dva računa istog vlasnika.
     *
     * @param paymentDto DTO sa detaljima transfera
     * @return ažurirana stanja oba računa
     */
    public UpdatedBalanceResponseDto transfer(PaymentDto paymentDto) {
        return restClient.post()
                .uri("/internal/accounts/transfer")
                .body(paymentDto)
                .retrieve()
                .body(UpdatedBalanceResponseDto.class);
    }

    /**
     * Izvršava transakciju novca između dva računa različitih vlasnika.
     *
     * @param paymentDto DTO sa detaljima transakcije
     * @return ažurirana stanja oba računa
     */
    public UpdatedBalanceResponseDto transaction(PaymentDto paymentDto) {
        return restClient.post()
                .uri("/internal/accounts/transaction")
                .body(paymentDto)
                .retrieve()
                .body(UpdatedBalanceResponseDto.class);
    }

    /**
     * Preuzima detaljne informacije o specifičnom računu.
     *
     * @param accountNumber broj računa
     * @return detaljne informacije o računu uključujući ID vlasnika
     */
    public AccountDetailsResponseDto getDetails(String accountNumber)
    {
        return restClient.get()
                .uri("/internal/accounts/{accountNumber}/details", accountNumber)
                .retrieve()
                .body(AccountDetailsResponseDto.class);
    }
}

package com.banka1.transaction_service.rest_client;



import com.banka1.transaction_service.domain.enums.CurrencyCode;
import com.banka1.transaction_service.dto.response.ConversionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * REST klijent za komunikaciju sa Exchange Service-om.
 * Obezbeđuje operacije za izračunavanje deviznih konverzija između valuta.
 */
@Service
public class ExchangeService {

    private final RestClient restClient;

    /**
     * Konstruktor koji injektuje REST klijenta za Exchange Service.
     *
     * @param restClient konfigurisan REST klijent sa JWT autentifikacijom
     */
    public ExchangeService(@Qualifier("exchangeClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Izračunava ekvivalentni iznos pri konverziji između dve valute.
     * <p>
     * Kalkulator koristi trenutne devizne kurseve i primenjuje komisiju.
     *
     * @param fromCurrency izvorna valuta
     * @param toCurrency ciljna valuta
     * @param amount iznos za konverziju u izvorenoj valuti
     * @return rezultat konverzije sa svim detaljima (konvertovani iznos, kurs, komisija)
     */
    public ConversionResponseDto calculate(CurrencyCode fromCurrency,
                                           CurrencyCode toCurrency,
                                           BigDecimal amount) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/calculate")
                        .queryParam("fromCurrency", fromCurrency.name())
                        .queryParam("toCurrency", toCurrency.name())
                        .queryParam("amount", amount)
                        .build())
                .retrieve()
                .body(ConversionResponseDto.class);
    }


}

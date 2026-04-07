package com.banka1.transaction_service.service;

import com.banka1.transaction_service.domain.enums.TransactionStatus;
import com.banka1.transaction_service.dto.request.NewPaymentDto;
import com.banka1.transaction_service.dto.response.ConversionResponseDto;
import com.banka1.transaction_service.dto.response.InfoResponseDto;
import com.banka1.transaction_service.dto.response.UpdatedBalanceResponseDto;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Interni interfejs koji definiše operacije upravljanja životnim ciklom transakcije.
 * Koristi se internally za kreiranje i završetak transakcija sa notifikacijama.
 */
public interface TransactionServiceInternal {

    /**
     * Kreira novu transakciju u bazi podataka sa svim relevantnim detaljima.
     * <p>
     * Ova metoda:
     * <ul>
     *   <li>Kreira Payment entitet sa svim podacima iz DTO-a</li>
     *   <li>Postavlja status na IN_PROGRESS</li>
     *   <li>Generiše jedinstveni redni broj kao "BANKA1-{id}"</li>
     *   <li>Čuva u bazi i vraća ID</li>
     * </ul>
     *
     * @param jwt JWT token korisnika koji inicira transakciju
     * @param newPaymentDto DTO sa detaljima plaćanja
     * @param infoResponseDto informacije o računima (vlasnici, valute, email)
     * @param conversionResponseDto rezultat devizne konverzije (iznos, kurs, komisija)
     * @return ID novo kreirane Payment entiteta
     */
    Long create(Jwt jwt, NewPaymentDto newPaymentDto, InfoResponseDto infoResponseDto, ConversionResponseDto conversionResponseDto);

    /**
     * Završava transakciju sa finalnim statusom i šalje email notifikaciju.
     * <p>
     * Ova metoda:
     * <ul>
     *   <li>Ažurira status transakcije na COMPLETED ili DENIED</li>
     *   <li>Registruje TransactionSynchronization callback</li>
     *   <li>Nakon commit-a, šalje RabbitMQ poruku za email notifikaciju</li>
     * </ul>
     *
     * @param jwt JWT token korisnika
     * @param infoResponseDto informacije o pošiljaocu (email, username)
     * @param id ID Payment entiteta koji se završava
     * @param transactionStatus finalni status transakcije (COMPLETED ili DENIED)
     */
    void finish(Jwt jwt, InfoResponseDto infoResponseDto, Long id, TransactionStatus transactionStatus);
}

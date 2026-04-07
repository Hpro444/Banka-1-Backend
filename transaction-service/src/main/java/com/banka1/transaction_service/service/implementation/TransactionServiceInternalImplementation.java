package com.banka1.transaction_service.service.implementation;

import com.banka1.transaction_service.domain.Payment;
import com.banka1.transaction_service.domain.enums.CurrencyCode;
import com.banka1.transaction_service.domain.enums.TransactionStatus;
import com.banka1.transaction_service.dto.request.NewPaymentDto;
import com.banka1.transaction_service.dto.response.ConversionResponseDto;
import com.banka1.transaction_service.dto.response.InfoResponseDto;
import com.banka1.transaction_service.dto.response.UpdatedBalanceResponseDto;
import com.banka1.transaction_service.rabbitMQ.EmailDto;
import com.banka1.transaction_service.rabbitMQ.EmailType;
import com.banka1.transaction_service.rabbitMQ.RabbitClient;
import com.banka1.transaction_service.repository.PaymentRepository;
import com.banka1.transaction_service.service.TransactionServiceInternal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

/**
 * Interni servis koji upravljanja životnim ciklom transakcije - kreiranjem, završetkom i notifikacijama.
 * Koristi se interno od TransactionServiceImplementation-a.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Setter
@Getter
@Transactional
public class TransactionServiceInternalImplementation implements TransactionServiceInternal {
    private final PaymentRepository paymentRepository;
    private final RabbitClient rabbitClient;

    /**
     * Kreira novu Payment transakciju u bazi podataka sa svim relevantnim detaljima.
     * <p>
     * Ova metoda:
     * <ul>
     *   <li>Kreira novi Payment entitet sa svim podatacima</li>
     *   <li>Postavlja status na IN_PROGRESS</li>
     *   <li>Čuva u bazi</li>
     *   <li>Generiše jedinstveni redni broj kao "BANKA1-{id}"</li>
     *   <li>Vraća ID novo kreirane transakcije</li>
     * </ul>
     *
     * @param jwt JWT token korisnika
     * @param newPaymentDto DTO sa detaljima plaćanja
     * @param infoResponseDto informacije o računima
     * @param conversionResponseDto rezultat devizne konverzije
     * @return ID novo kreirane Payment transakcije
     */
    @Override
    public Long create(Jwt jwt, NewPaymentDto newPaymentDto, InfoResponseDto infoResponseDto, ConversionResponseDto conversionResponseDto) {
        Payment payment=new Payment();
        payment.setFromAccountNumber(newPaymentDto.getFromAccountNumber());
        payment.setToAccountNumber(newPaymentDto.getToAccountNumber());
        payment.setStatus(TransactionStatus.IN_PROGRESS);
        payment.setInitialAmount(newPaymentDto.getAmount());
        payment.setRecipientClientId(infoResponseDto.getToVlasnik());
        payment.setFinalAmount(conversionResponseDto.toAmount());
        payment.setCommission(conversionResponseDto.commission());
        payment.setRecipientName(newPaymentDto.getRecipientName());
        payment.setPaymentCode(newPaymentDto.getPaymentCode());
        payment.setReferenceNumber(newPaymentDto.getReferenceNumber());
        payment.setPaymentPurpose(newPaymentDto.getPaymentPurpose());
        payment.setFromCurrency(CurrencyCode.valueOf(conversionResponseDto.fromCurrency().toUpperCase()));
        payment.setToCurrency(CurrencyCode.valueOf(conversionResponseDto.toCurrency().toUpperCase()));
        payment.setExchangeRate(conversionResponseDto.rate());
        payment=paymentRepository.save(payment);
        payment.setOrderNumber("BANKA1-"+payment.getId());
        return payment.getId();
    }

    /**
     * Završava transakciju sa finalnim statusom i šalje email notifikaciju.
     * <p>
     * Procesa:
     * <ul>
     *   <li>Pronalazi Payment entitet po ID-u</li>
     *   <li>Ažurira status na COMPLETED ili DENIED</li>
     *   <li>Registruje TransactionSynchronization callback</li>
     *   <li>Nakon SQL commit-a, šalje RabbitMQ poruku za email notifikaciju</li>
     * </ul>
     *
     * @param jwt JWT token korisnika
     * @param infoResponseDto informacije o pošiljaocu (email, username)
     * @param id ID Payment entiteta koji se završava
     * @param transactionStatus finalni status transakcije
     */
    @Override
    public void finish(Jwt jwt, InfoResponseDto infoResponseDto, Long id, TransactionStatus transactionStatus) {
        Payment payment=paymentRepository.findById(id).orElseThrow(()->new IllegalStateException("Greska u sistemu, nije sacuvao entitet"));
        payment.setStatus(transactionStatus);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rabbitClient.sendEmailNotification(new EmailDto(infoResponseDto.getFromUsername(),infoResponseDto.getFromEmail(), (transactionStatus==TransactionStatus.COMPLETED)?EmailType.TRANSACTION_COMPLETED:EmailType.TRANSACTION_DENIED));

            }
        });

    }

    /**
     * Periodički cleanup task koji detektuje i obeležava "zaglavljene" transakcije kao DENIED.
     * <p>
     * Izvršava se svakih 100 sekundi i pronalazi transakcije koje su ostale
     * u IN_PROGRESS statusu duže od 5 minuta.
     */
    @Scheduled(fixedRate = 100000)
    public void cleanup() {
        int updated = paymentRepository.markStuckPayments(
                TransactionStatus.IN_PROGRESS,
                TransactionStatus.DENIED,
                LocalDateTime.now().minusMinutes(5)
        );

        if (updated > 0) {
            log.warn("Marked {} stuck payments as DENIED", updated);
        }
    }


}

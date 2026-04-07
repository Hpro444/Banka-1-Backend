package com.banka1.transaction_service.service.implementation;

import com.banka1.transaction_service.domain.Payment;
import com.banka1.transaction_service.domain.enums.TransactionStatus;
import com.banka1.transaction_service.dto.request.ApproveDto;
import com.banka1.transaction_service.dto.request.NewPaymentDto;
import com.banka1.transaction_service.dto.request.PaymentDto;
import com.banka1.transaction_service.dto.response.*;
import com.banka1.transaction_service.exception.BusinessException;
import com.banka1.transaction_service.exception.ErrorCode;
import com.banka1.transaction_service.rabbitMQ.EmailDto;
import com.banka1.transaction_service.rabbitMQ.EmailType;
import com.banka1.transaction_service.rabbitMQ.RabbitClient;
import com.banka1.transaction_service.repository.PaymentRepository;
import com.banka1.transaction_service.rest_client.AccountService;
import com.banka1.transaction_service.rest_client.ClientService;
import com.banka1.transaction_service.rest_client.ExchangeService;
import com.banka1.transaction_service.rest_client.VerificationService;
import com.banka1.transaction_service.service.TransactionService;
import com.banka1.transaction_service.service.TransactionServiceInternal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Implementacija servisa za upravljanje transakcijama.
 * <p>
 * Ova klasa sadrži kompletnu poslovnu logiku za kreiranje, pretragu i filtriranje transakcija (plaćanja)
 * uključujući validacije, komunikaciju sa eksternim servisima i rad sa bazom podataka.
 */
@RequiredArgsConstructor
@Getter
@Setter
@Service
@Slf4j
public class TransactionServiceImplementation implements TransactionService {

    private final ExchangeService exchangeService;
    private final VerificationService verificationService;
    private final AccountService accountService;
    private final ClientService clientService;
    @Value("${banka.security.id}")
    private String appPropertiesId;
    @Value("${banka.security.roles-claim}")
    private String roles;
    @Value("${transaction.verification.skip:true}")
    private boolean skipVerification;
    private final TransactionServiceInternal transactionServiceInternal;
    private final PaymentRepository paymentRepository;

    /**
     * Kreira novu transakciju (plaćanje) sa kompletan poslovnom logikom.
     * <p>
     * Proces:
     * <ul>
     *   <li>Provera verifikacije klijenta (ako je uključena)</li>
     *   <li>Pronalaženje računa i validacija</li>
     *   <li>Izračunavanje devizne konverzije</li>
     *   <li>Kreiranje Payment entiteta u bazi</li>
     *   <li>3 pokušaja izvršavanja transfera sa retry logikom</li>
     *   <li>Ažuriranje statusa i slanje email notifikacije</li>
     * </ul>
     *
     * @param jwt JWT token autentifikovanog korisnika
     * @param newPaymentDto DTO sa detaljima novog plaćanja
     * @return odgovor sa statusom plaćanja i porukom
     */
    @Override
    public NewPaymentResponseDto newPayment(Jwt jwt, NewPaymentDto newPaymentDto) {
        if (!skipVerification) {
            VerificationStatusResponse verificationStatusResponse = verificationService.getStatus(newPaymentDto.getVerificationSessionId());
            if (verificationStatusResponse == null || !verificationStatusResponse.isVerified())
                throw new BusinessException(ErrorCode.VERIFICATION_FAILED, ErrorCode.VERIFICATION_FAILED.getTitle());
        } else {
            log.warn("SKIP_VERIFICATION=true, preskačem proveru verifikacionog koda");
        }
        InfoResponseDto infoResponseDto;
        try {
            infoResponseDto = accountService.getInfo(newPaymentDto.getFromAccountNumber(),newPaymentDto.getToAccountNumber());
        } catch (HttpClientErrorException ex) {
            if (isMissingAccountError(ex)) {
                throw new NoSuchElementException("Account number does not exist");
            }
            throw ex;
        }
        if(infoResponseDto == null)
            throw new IllegalStateException("Greska sa account servisom");
        ConversionResponseDto conversionResponseDto=exchangeService.calculate(infoResponseDto.getFromCurrencyCode(),infoResponseDto.getToCurrencyCode(),newPaymentDto.getAmount());
        if(conversionResponseDto == null)
            throw new IllegalStateException("Greska sa account servisom");
        Long id=transactionServiceInternal.create(jwt,newPaymentDto,infoResponseDto,conversionResponseDto);
        UpdatedBalanceResponseDto updatedBalanceResponseDto=null;
        TransactionStatus transactionStatus = TransactionStatus.DENIED;
        PaymentDto paymentDto = new PaymentDto(newPaymentDto.getFromAccountNumber(), newPaymentDto.getToAccountNumber(), conversionResponseDto.fromAmount(), conversionResponseDto.toAmount(), conversionResponseDto.commission(), ((Number) jwt.getClaim(appPropertiesId)).longValue());
        boolean sameOwner = infoResponseDto.getFromVlasnik().equals(infoResponseDto.getToVlasnik());
        for(int i=0;i<3;i++) {
            try {
                if (sameOwner) {
                    updatedBalanceResponseDto = accountService.transfer(paymentDto);
                } else {
                    updatedBalanceResponseDto = accountService.transaction(paymentDto);
                }
                transactionStatus=TransactionStatus.COMPLETED;
                break;
            } catch (RestClientException e) {
                log.warn("Transfer failed attempt {}", i, e);
            }
        }
        transactionServiceInternal.finish(jwt,infoResponseDto,id, transactionStatus);
        if(transactionStatus==TransactionStatus.COMPLETED)
            return new NewPaymentResponseDto("Uspesan payment", transactionStatus.name());
        return new NewPaymentResponseDto("Payment nije bio uspesan", transactionStatus.name());
    }

    /**
     * Proverava da li je HTTP greška rezultat nepostojeceg računa.
     * Koristi se za razlikovanje grešaka od Account servisa.
     *
     * @param ex HTTP greška iz Account servisa
     * @return true ako je greška razlog "račun ne postoji", false inače
     */
    private boolean isMissingAccountError(HttpClientErrorException ex) {
        if (HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
            return true;
        }

        if (!HttpStatus.BAD_REQUEST.equals(ex.getStatusCode())) {
            return false;
        }

        String body = ex.getResponseBodyAsString();
        if (body == null) {
            return false;
        }

        String normalized = body.toLowerCase(Locale.ROOT);
        return normalized.contains("ne postoji") && normalized.contains("racun");
    }

    /**
     * Preuzima sve transakcije za određeni račun klijenta sa autentifikacijom.
     * <p>
     * Samo vlasnik računa može pristupiti ovoj metodi.
     *
     * @param jwt JWT token autentifikovanog korisnika
     * @param accountNumber broj računa
     * @param page redni broj stranice
     * @param size broj stavki po stranici
     * @return paginirana lista transakcija za dati račun
     */
    @Transactional
    @Override
    public Page<TransactionResponseDto> findAllTransactions(Jwt jwt, String accountNumber, int page, int size) {
        AccountDetailsResponseDto accountDetailsResponseDto=accountService.getDetails(accountNumber);
        if(accountDetailsResponseDto == null)
            throw new IllegalStateException("Sistemska greska");
        if(accountDetailsResponseDto.getVlasnik()==null || !accountDetailsResponseDto.getVlasnik().equals(((Number) jwt.getClaim(appPropertiesId)).longValue()))
            throw new IllegalArgumentException("Nisi vlasnik racuna");
        return paymentRepository.findByAccountNumber(accountNumber, PageRequest.of(page,size)).map(TransactionResponseDto::new);
    }

    /**
     * Preuzima transakcije sa naprednom filtracijom - dostupno samo zaposlenima i vlasnicima.
     *
     * @param jwt JWT token
     * @param accountNumber broj računa (opciono)
     * @param transactionStatus status transakcije (opciono)
     * @param fromDate početna datuma (opciono)
     * @param toDate krajnja datuma (opciono)
     * @param initialAmountMin minimalni početni iznos (opciono)
     * @param initialAmountMax maksimalni početni iznos (opciono)
     * @param finalAmountMin minimalni finalni iznos (opciono)
     * @param finalAmountMax maksimalni finalni iznos (opciono)
     * @param page redni broj stranice
     * @param size broj stavki po stranici
     * @return filtrirana i paginirana lista transakcija
     */
    @Override
    public Page<TransactionResponseDto> findPayments(Jwt jwt, String accountNumber, TransactionStatus transactionStatus, LocalDateTime fromDate, LocalDateTime toDate, BigDecimal initialAmountMin, BigDecimal initialAmountMax, BigDecimal finalAmountMin, BigDecimal finalAmountMax, int page, int size) {
        if(!jwt.getClaimAsString(roles).equalsIgnoreCase("ADMIN"))
        {
        AccountDetailsResponseDto accountDetailsResponseDto=accountService.getDetails(accountNumber);
        if(accountDetailsResponseDto == null)
            throw new IllegalStateException("Sistemska greska");
        if(accountDetailsResponseDto.getVlasnik()==null || !accountDetailsResponseDto.getVlasnik().equals(((Number) jwt.getClaim(appPropertiesId)).longValue()))
            throw new IllegalArgumentException("Nisi vlasnik racuna");
        }
        Specification<Payment> specification = Specification.unrestricted();

        if (accountNumber != null) {
            specification = specification.and((root, query, cb) ->
                    cb.or(
                            cb.equal(root.get("fromAccountNumber"), accountNumber),
                            cb.equal(root.get("toAccountNumber"), accountNumber)
                    ));
        }
        if (transactionStatus != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("status"), transactionStatus));
        }
        if (fromDate != null) {
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
        }
        if (toDate != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
        }
        if (initialAmountMin != null) {
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("initialAmount"), initialAmountMin));
        }
        if (initialAmountMax != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("initialAmount"), initialAmountMax));
        }
        if (finalAmountMin != null) {
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("finalAmount"), finalAmountMin));
        }
        if (finalAmountMax != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("finalAmount"), finalAmountMax));
        }

        return paymentRepository.findAll(
                        specification,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(TransactionResponseDto::new);
    }

    /**
     * Preuzima sve transakcije za određeni račun - zaposlenski pristup bez ograničenja vlasnika.
     *
     * @param accountNumber broj računa
     * @param page redni broj stranice
     * @param size broj stavki po stranici
     * @return paginirana lista transakcija
     */
    @Transactional
    @Override
    public Page<TransactionResponseDto> findAllTransactionsForEmployee(String accountNumber, int page, int size) {
        return paymentRepository.findByAccountNumber(accountNumber, PageRequest.of(page, size))
                .map(TransactionResponseDto::new);
    }

    //todo za sad ovo ostavljam ovde, validacije bi trebalo da budu zaseban servis, if-ove sam ostavio just in case
    //TODO menjati exceptione

//    private void  validation(AccountDto account,Jwt jwt)
//    {
//        if(account==null)
//            throw new IllegalArgumentException("Ne postoji unet racun");
//        if(!account.getVlasnik().equals(((Number) jwt.getClaim(appPropertiesId)).longValue()))
//            throw new IllegalArgumentException("Nisi vlasnik racuna");
//    }

}

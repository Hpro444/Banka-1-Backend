package com.banka1.transaction_service.service;

import com.banka1.transaction_service.domain.enums.TransactionStatus;
import com.banka1.transaction_service.dto.request.ApproveDto;
import com.banka1.transaction_service.dto.request.NewPaymentDto;
import com.banka1.transaction_service.dto.response.NewPaymentResponseDto;
import com.banka1.transaction_service.dto.response.TransactionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Interfejs koji definiše poslovnu logiku za upravljanje transakcijama.
 * Obezbeđuje operacije za kreiranje, pregled i filtriranje plaćanja.
 */
public interface TransactionService {

    /**
     * Kreira novu transakciju (plaćanje) između dva računa.
     *
     * @param jwt JWT token autentifikovanog korisnika
     * @param newPaymentDto DTO sa detaljima novog plaćanja
     * @return odgovor sa statusom i porukom o plaćanju
     */
    NewPaymentResponseDto newPayment(Jwt jwt, NewPaymentDto newPaymentDto);

    /**
     * Preuzima sve transakcije za specifičan račun autentifikovanog korisnika.
     *
     * @param jwt JWT token autentifikovanog korisnika
     * @param accountNumber broj računa čije transakcije se preuzimaju
     * @param page redni broj stranice
     * @param size broj stavki po stranici
     * @return paginirana lista transakcija
     */
    Page<TransactionResponseDto> findAllTransactions(Jwt jwt, String accountNumber, int page, int size);

    /**
     * Preuzima transakcije sa naprednom filtracijom po različitim kriterijumima.
     *
     * @param jwt JWT token autentifikovanog korisnika
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
    Page<TransactionResponseDto> findPayments(Jwt jwt, String accountNumber, TransactionStatus transactionStatus, LocalDateTime fromDate, LocalDateTime toDate, BigDecimal initialAmountMin, BigDecimal initialAmountMax, BigDecimal finalAmountMin, BigDecimal finalAmountMax, int page, int size);

    /**
     * Preuzima sve transakcije za specifičan račun (zaposlenski pristup - bez ograničenja vlasnika).
     *
     * @param accountNumber broj računa čije transakcije se preuzimaju
     * @param page redni broj stranice
     * @param size broj stavki po stranici
     * @return paginirana lista transakcija
     */
    Page<TransactionResponseDto> findAllTransactionsForEmployee(String accountNumber, int page, int size);

}

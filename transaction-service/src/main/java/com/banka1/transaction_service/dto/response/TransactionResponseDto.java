package com.banka1.transaction_service.dto.response;

import com.banka1.transaction_service.domain.Payment;
import com.banka1.transaction_service.domain.enums.CurrencyCode;
import com.banka1.transaction_service.domain.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO odgovora za transakciju - vraća se klijentu kod pregleda istorije transakcija.
 * Sadrži sve relevantne informacije o izvršenoj ili izvršavajućoj transakciji.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionResponseDto {

    /** Jedinstveni redni broj plaćanja u sistemu */
    private String orderNumber;

    /** Broj računa sa kojeg je novac prenet */
    private String fromAccountNumber;

    /** Broj računa na koji je novac prenet */
    private String toAccountNumber;

    /** Početni iznos u izvorenoj valuti */
    private BigDecimal initialAmount;

    /** Finalni iznos u ciljnoj valuti */
    private BigDecimal finalAmount;

    /** Ime primaoca novca */
    private String recipientName;

    /** Šifra plaćanja */
    private String paymentCode;

    /** Referentni broj plaćanja */
    private String referenceNumber;

    /** Svrha/opis plaćanja */
    private String paymentPurpose;

    /** Trenutni status transakcije */
    private TransactionStatus status;

    /** Valuta izvorne transakcije */
    private CurrencyCode fromCurrency;

    /** Valuta odredišne transakcije */
    private CurrencyCode toCurrency;

    /** Kurs konverzije između valuta */
    private BigDecimal exchangeRate;

    /** Vreme kreiranja transakcije */
    private LocalDateTime createdAt;

    /**
     * Konstruktor za konverziju Payment entiteta u DTO.
     *
     * @param payment Payment entitet iz baze podataka
     */
    public TransactionResponseDto(Payment payment) {
        this.orderNumber = payment.getOrderNumber();
        this.fromAccountNumber = payment.getFromAccountNumber();
        this.toAccountNumber = payment.getToAccountNumber();
        this.initialAmount = payment.getInitialAmount();
        this.finalAmount = payment.getFinalAmount();
        this.recipientName = payment.getRecipientName();
        this.paymentCode = payment.getPaymentCode();
        this.referenceNumber = payment.getReferenceNumber();
        this.paymentPurpose = payment.getPaymentPurpose();
        this.status = payment.getStatus();
        this.fromCurrency = payment.getFromCurrency();
        this.toCurrency = payment.getToCurrency();
        this.exchangeRate = payment.getExchangeRate();
        this.createdAt = payment.getCreatedAt();
    }
}

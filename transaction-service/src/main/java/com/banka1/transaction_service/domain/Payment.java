package com.banka1.transaction_service.domain;

import com.banka1.transaction_service.domain.base.BaseEntityWithoutDelete;
import com.banka1.transaction_service.domain.enums.CurrencyCode;
import com.banka1.transaction_service.domain.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


//Ideja sa BaseEntityWithoutDelete je da se ohrabri dobar dizajn pattern,
  //ukoliko ispadne da je Payment jedini sa ovim, obrisacu ga

/**
 * JPA entitet koji predstavlja finansijsku transakciju (plaćanje) između dva računa.
 * Sadrži sve relevantne podatke o transakciji uključujući identifikacione podatke,
 * finansijske podatke, podatke o valutama i status transakcije.
 * Nasledjuje BaseEntityWithoutDelete što znači da nema soft delete zastavice.
 */
@Entity
@Table(
        name = "payment_table"
)

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment extends BaseEntityWithoutDelete {

    /** Jedinstveni redni broj plaćanja - automatski generiše se kao "BANKA1-{id}" */
    @Column(unique = true)
    private String orderNumber;

    /** Broj računa sa kojeg se novac prenosi (19 cifara) */
    @NotBlank
    @Column(nullable = false)
    private String fromAccountNumber;

    /** Broj računa na koji se novac prenosi (19 cifara) */
    @NotBlank
    @Column(nullable = false)
    private String toAccountNumber;

    /** Iznos u izvorenoj valuti */
    @Column(nullable = false)
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal initialAmount;

    /** Iznos u ciljnoj valuti nakon konverzije */
    @Column(nullable = false)
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal finalAmount;

    /** Komisija naplaćena za transakciju */
    @Column(nullable = false)
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal commission;

    /** ID klijenta koji je primalac novca */
    @Column(nullable = false)
    private Long recipientClientId;

    /** Ime primaoca novca */
    @NotBlank
    @Column(nullable = false)
    private String recipientName;

    /** Šifra plaćanja - mora počinjati sa 2 i imati tačno 3 cifre */
    @NotBlank
    @Pattern(regexp = "^2.*", message = "Sifra mora poceti sa 2")
    @Pattern(regexp = "^\\d{3}$", message = "Sifra mora imati tacno 3 cifre")
    @Column(nullable = false)
    private String paymentCode;

    /** Referentni broj plaćanja (opciono) */
    private String referenceNumber;

    /** Svrha/opis plaćanja */
    @NotBlank
    @Column(nullable = false)
    private String paymentPurpose;

    /** Trenutni status transakcije */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status=TransactionStatus.IN_PROGRESS;

    /** Valuta izvorne transakcije */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode fromCurrency;

    /** Valuta odredišne transakcije */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode toCurrency;

    /** Kurs konverzije između valuta (toAmount / fromAmount) */
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal exchangeRate;

}

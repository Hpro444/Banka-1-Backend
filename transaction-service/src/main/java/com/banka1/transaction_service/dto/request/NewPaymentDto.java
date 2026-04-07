package com.banka1.transaction_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO za zahtev kreiranja novog plaćanja od strane klijenta.
 * <p>
 * Sadrži sve osnovne informacije potrebne za inicijalizovanje transakcije:
 * broja računa pošiljaoca i primaoca, iznos, podatke o primaocu,
 * šifru i svrhu plaćanja, te ID verifikacijske sesije.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewPaymentDto {

    /** Broj računa sa kojeg se šalje novac - mora biti 19-cifreni */
    @NotBlank(message = "Unesi racun posiljaoca")
    @Pattern(regexp = "^\\d{19}$", message = "Broj racuna mora imati 19 cifara")
    private String fromAccountNumber;

    /** Broj računa na koji se šalje novac - mora biti 19-cifreni */
    @NotBlank(message = "Unesi racun primaoca")
    @Pattern(regexp = "^\\d{19}$", message = "Broj racuna mora imati 19 cifara")
    private String toAccountNumber;

    /** Iznos koji se šalje u osnovnoj valuti */
    @NotNull(message = "Unesi iznos")
    private BigDecimal amount;

    /** Ime i prezime primaoca novca */
    @NotBlank(message = "Unesi naziv primaoca")
    private String recipientName;

    /** Šifra plaćanja - mora počinjati sa 2 i imati tačno 3 cifre */
    @NotNull(message = "Unesi sifru placanja")
    @Pattern(regexp = "^2.*", message = "Sifra mora poceti sa 2")
    @Pattern(regexp = "^\\d{3}$", message = "Sifra mora imati tacno 3 cifre")
    private String paymentCode;

    /** Referentni broj plaćanja (opciono - može biti broj i slova) */
    private String referenceNumber;

    /** Svrha/opis plaćanja */
    @NotBlank(message = "Unesi svrhu placanja")
    private String paymentPurpose;

    /** ID verifikacijske sesije koju je klijent prošao */
    @NotNull(message = "Unesi verification session ID")
    private Long verificationSessionId;

}

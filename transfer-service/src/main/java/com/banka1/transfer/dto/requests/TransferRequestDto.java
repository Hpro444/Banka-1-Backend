package com.banka1.transfer.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Ulazni podaci od strane klijenta za iniciranje novog transfera.
 */
@Data
public class TransferRequestDto {
    @NotBlank
    private String fromAccountNumber; // Izvorni račun (mora biti popunjen)

    @NotBlank
    private String toAccountNumber; // Ciljni račun (mora biti popunjen)

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be strictly positive") // Iznos transfera (pozitivna vrednost)
    private BigDecimal amount;

    @NotBlank
    private String verificationCode;  // 2FA kod dobijen putem emaila/SMS-a

    @NotBlank
    private String verificationSessionId; // ID sesije vezan za 2FA kod
}

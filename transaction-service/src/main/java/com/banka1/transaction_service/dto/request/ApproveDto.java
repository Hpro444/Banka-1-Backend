package com.banka1.transaction_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO za zahtev odobrenja/verifikacije sa sigurnosnim ključem.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApproveDto {

    /** Sigurnosni ključ ili verifikacijski kod */
    @NotBlank(message = "Unesi key")
    private String key;
}

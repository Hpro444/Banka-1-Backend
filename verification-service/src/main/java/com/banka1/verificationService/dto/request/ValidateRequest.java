package com.banka1.verificationService.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO za zahtev validacije verifikacionog koda.
 * Sadrži ID sesije i kod koji je dao klijent.
 */
@Getter
@Setter
public class ValidateRequest {
    /** ID sesije verifikacije za validaciju. */
    private Long sessionId;

    /** Verifikacioni kod koji je uneo klijent. */
    private String code;
}

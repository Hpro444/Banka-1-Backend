package com.banka1.verificationService.dto.request;

import com.banka1.verificationService.model.enums.OperationType;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO za zahtev generisanja nove sesije verifikacije.
 * Sadrži neophodne informacije za kreiranje i slanje verifikacionog koda.
 */
@Getter
@Setter
public class GenerateRequest {
    /** ID klijenta koji zahteva verifikaciju. */
    private Long clientId;

    /** Tip operacije koja zahteva verifikaciju. */
    private OperationType operationType;

    /** Opcioni ID povezanog entiteta (npr., ID transakcije ili zahteva). */
    private String relatedEntityId;
}

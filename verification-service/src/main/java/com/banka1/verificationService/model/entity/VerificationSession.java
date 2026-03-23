package com.banka1.verificationService.model.entity;

import com.banka1.verificationService.model.enums.OperationType;
import com.banka1.verificationService.model.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entitet koji predstavlja sesiju verifikacije u bazi podataka.
 * Čuva heširane verifikacione kodove, metapodatke sesije i status za 2FA operacije.
 */
@Entity
@Table(name = "verification_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationSession {

    /** Jedinstveni identifikator sesije verifikacije, auto-generisan od strane baze podataka. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID klijenta koji zahteva verifikaciju. */
    private Long clientId;

    /** Heširani verifikacioni kod za sigurnost; nikada ne čuva običan tekst. */
    @Column(nullable = false)
    private String code; // hashed

    /** Tip operacije koja se verifikuje (npr., PAYMENT, TRANSFER). */
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    /** Opcioni ID povezanog entiteta (npr., ID transakcije). */
    private String relatedEntityId;

    /** Vreme kada je sesija kreirana. */
    private LocalDateTime createdAt;

    /** Vreme kada sesija ističe (tipično 5 minuta nakon kreiranja). */
    private LocalDateTime expiresAt;

    /** Broj neuspjelih pokušaja validacije; sesija se otkazuje nakon 3. */
    private int attemptCount;

    /** Trenutni status sesije verifikacije. */
    @Enumerated(EnumType.STRING)
    private VerificationStatus status;
}

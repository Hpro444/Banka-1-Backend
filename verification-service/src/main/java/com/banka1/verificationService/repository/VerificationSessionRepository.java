package com.banka1.verificationService.repository;

import com.banka1.verificationService.model.entity.VerificationSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA interfejs repozitorijuma za upravljanje entitetima VerificationSession.
 * Pruža CRUD operacije i metode upita za sesije verifikacije.
 */
public interface VerificationSessionRepository extends JpaRepository<VerificationSession, Long> {
}

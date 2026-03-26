package com.banka1.transaction_service.repository;

import com.banka1.transaction_service.domain.Payment;
import com.banka1.transaction_service.domain.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    @Modifying
    @Query("""
    UPDATE Payment p
    SET p.status = :newStatus
    WHERE p.status = :oldStatus
    AND p.createdAt < :threshold
""")
    int markStuckPayments(
            TransactionStatus oldStatus,
            TransactionStatus newStatus,
            LocalDateTime threshold
    );



}

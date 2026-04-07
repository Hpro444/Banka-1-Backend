package com.banka1.transaction_service.repository;

import com.banka1.transaction_service.domain.Payment;
import com.banka1.transaction_service.domain.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Spring Data JPA repository za Payment entitet.
 * Obezbeđuje CRUD operacije i custom query metode za upravljanje transakcijama.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long>, JpaSpecificationExecutor<Payment> {

    /**
     * Ažurira status "zaglavl enih" transakcija koje su ostale u IN_PROGRESS statusu
     * duže od specificiranog vremenskog praga.
     * <p>
     * Ova metoda se koristi u cleanup taskovima koji detektuju i rešavaju
     * neuspešno završene transakcije.
     *
     * @param oldStatus stari status koji se menja (obično IN_PROGRESS)
     * @param newStatus novi status u koji se prevodimo (obično DENIED)
     * @param threshold vremenski prag - transakcije kreiran pre ovog vremena
     * @return broj ažuriranih redova
     */
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

    /**
     * Preuzima sve transakcije povezane sa određenim brojem računa.
     * <p>
     * Pronalazi sve transakcije gde je dati račun ili pošiljaoc ili primalac,
     * sortiran po vremenu kreiranja (najnovije prvo).
     *
     * @param accountNumber broj računa
     * @param pageable parametri paginacije
     * @return paginirana lista transakcija za dati račun
     */
    @Query("""
    SELECT p
    FROM Payment p
    WHERE p.fromAccountNumber = :accountNumber
       OR p.toAccountNumber = :accountNumber
    ORDER BY p.createdAt DESC
""")
    Page<Payment> findByAccountNumber(
            @Param("accountNumber") String accountNumber,
            Pageable pageable
    );
}

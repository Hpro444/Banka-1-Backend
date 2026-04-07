package com.banka1.transaction_service.domain.enums;

/**
 * Enum koji predstavlja moguće statuse finansijske transakcije tokom njene životnog ciklusa.
 * <p>
 * Statusi mogu biti:
 * <ul>
 *   <li>IN_PROGRESS - Transakcija je prihvaćena i u procesu izvršavanja</li>
 *   <li>COMPLETED - Transakcija je uspešno izvršena</li>
 *   <li>DENIED - Transakcija je odbijena ili nije mogla biti izvršena</li>
 * </ul>
 */
public enum TransactionStatus {
    /** Transakcija je prihvaćena i u procesu izvršavanja */
    IN_PROGRESS,
    /** Transakcija je uspešno izvršena */
    COMPLETED,
    /** Transakcija je odbijena ili nije mogla biti izvršena */
    DENIED
}

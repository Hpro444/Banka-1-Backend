package com.banka1.verificationService.model.enums;

/**
 * Enumeration of operation types that require verification.
 *
 * Categorizes the different kinds of transactions and actions that trigger
 * two-factor authentication (2FA). Each operation type may have different
 * verification requirements or processing workflows.
 */
public enum OperationType {
    /**
     * A payment transaction requiring verification before processing.
     * Typically initiated by the client to transfer funds to a third party.
     */
    PAYMENT,

    /**
     * A money transfer between accounts (internal or external).
     * Requires verification to ensure the recipient is correct.
     */
    TRANSFER,

    /**
     * A change to spending limits on an account.
     * Requires verification before applying the new limit threshold.
     */
    LIMIT_CHANGE,

    /**
     * A request for a new credit or debit card.
     * Requires verification to prevent fraudulent card issuance.
     */
    CARD_REQUEST,

    /**
     * A loan application or credit request.
     * Requires verification as part of the loan underwriting process.
     */
    LOAN_REQUEST
}

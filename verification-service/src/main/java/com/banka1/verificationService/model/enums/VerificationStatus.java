package com.banka1.verificationService.model.enums;

/**
 * Enumeration representing the possible states of a verification session.
 *
 * Tracks the lifecycle of a session from creation through completion or failure.
 * A session progresses through these states as the user interacts with the verification system.
 */
public enum VerificationStatus {
    /**
     * Session has been created and is awaiting code validation.
     * The user has received the OTP code via email and has 5 minutes to submit it.
     * This is the initial state of every new verification session.
     */
    PENDING,

    /**
     * The submitted code was correctly validated and the session is now complete.
     * No further validation attempts are allowed; the operation may proceed.
     * This is a terminal, successful state.
     */
    VERIFIED,

    /**
     * The session has expired due to exceeding the time limit (default 5 minutes).
     * The user cannot validate the code anymore and must request a new verification session.
     * This is a terminal, unsuccessful state.
     */
    EXPIRED,

    /**
     * The session has been cancelled due to too many failed validation attempts (3 or more).
     * The user cannot validate the code anymore and must request a new verification session.
     * This is a terminal, unsuccessful state.
     */
    CANCELLED
}

package com.banka1.verificationService.dto.response;

import com.banka1.verificationService.model.enums.VerificationStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for the response after validating a verification code.
 *
 * Provides comprehensive feedback on the validation result, including whether the code
 * was correct, the current session state, and the number of remaining validation attempts.
 */
@Getter
@Setter
public class ValidateResponse {
    /**
     * Whether the provided code matched the stored hash.
     * true if the code was correct; false otherwise.
     */
    private boolean valid;

    /**
     * The current status of the verification session after validation.
     * Indicates whether the session is still PENDING, VERIFIED, CANCELLED, or EXPIRED.
     *
     * @see VerificationStatus
     */
    private VerificationStatus status;

    /**
     * Number of validation attempts remaining before session cancellation.
     * Decrements with each failed attempt. When this reaches 0, the session is cancelled.
     * Returns 0 if the session is already VERIFIED, CANCELLED, or EXPIRED.
     */
    private int remainingAttempts;

    /**
     * Constructs a validation response with the outcome details.
     *
     * @param valid true if the code matched the stored hash; false for incorrect codes
     * @param status the current state of the verification session
     * @param remainingAttempts the number of attempts left before automatic cancellation
     */
    public ValidateResponse(boolean valid, VerificationStatus status, int remainingAttempts) {
        this.valid = valid;
        this.status = status;
        this.remainingAttempts = remainingAttempts;
    }
}

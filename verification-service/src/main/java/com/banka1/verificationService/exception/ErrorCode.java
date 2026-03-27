package com.banka1.verificationService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enumeration that centralizes all business error codes for the verification service.
 *
 * Each constant encapsulates an HTTP status code, machine-readable error code, and
 * human-readable title. These are used by {@link BusinessException} and returned to clients
 * via {@link GlobalExceptionHandler} as structured error responses.
 *
 * Error codes follow the pattern: ERR_VERIFICATION_XXX for verification-specific errors.
 */
@Getter
public enum ErrorCode {

    // ── Verification Session Errors (ERR_VERIFICATION_xxx) ──────────────────────────────────

    /**
     * The requested verification session does not exist in the database.
     * Returned when a client attempts to validate or check status of a non-existent session ID.
     * HTTP Status: 404 Not Found.
     */
    VERIFICATION_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_VERIFICATION_001", "Sesija verifikacije nije pronađena"),

    /**
     * The verification session has been cancelled.
     * Indicates the session was cancelled due to exceeding the maximum failed attempt limit (3).
     * Returned when a client attempts to use a cancelled session.
     * HTTP Status: 400 Bad Request.
     */
    VERIFICATION_SESSION_CANCELLED(HttpStatus.BAD_REQUEST, "ERR_VERIFICATION_002", "Sesija verifikacije je otkazana"),

    /**
     * The verification session has already been verified.
     * Indicates the code was already successfully validated in a previous request.
     * Returned when a client attempts to validate again after successful validation.
     * HTTP Status: 400 Bad Request.
     */
    VERIFICATION_SESSION_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "ERR_VERIFICATION_003", "Sesija verifikacije je već verifikovana"),

    /**
     * The verification code has expired.
     * Indicates the session's time window (default 5 minutes) has been exceeded.
     * Returned when a client attempts to validate after expiration.
     * HTTP Status: 400 Bad Request.
     */
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "ERR_VERIFICATION_004", "Verifikacioni kod je istekao"),

    /**
     * The verification code is invalid.
     * Indicates the submitted code does not match the stored hash.
     * Currently defined but may be superseded by validation failures with attempt counter feedback.
     * HTTP Status: 400 Bad Request.
     */
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "ERR_VERIFICATION_005", "Neispravan verifikacioni kod"),

    /**
     * A PENDING verification session already exists for the same operation.
     * Indicates a unique constraint violation: a session with the same clientId, operationType,
     * and relatedEntityId already exists in PENDING state. The client must wait for the existing
     * session to expire or be cancelled before creating a new one.
     * HTTP Status: 409 Conflict.
     */
    VERIFICATION_SESSION_ALREADY_PENDING(HttpStatus.CONFLICT, "ERR_VERIFICATION_006", "Aktivna verifikaciona sesija vec postoji"),

    /**
     * Access denied due to insufficient privileges.
     * Returned when a client attempts to verify on behalf of another user
     * (clientId in request does not match authenticated user).
     * HTTP Status: 403 Forbidden.
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "ERR_FORBIDDEN", "Pristup odbijen");

    /**
     * The HTTP status code to return to the client when this error occurs.
     * Guides the client on how to handle the error.
     */
    private final HttpStatus httpStatus;

    /**
     * A stable, machine-readable error code.
     * Used for client-side error handling and logging.
     * Format: ERR_SECTION_NUMBER (e.g., ERR_VERIFICATION_001).
     */
    private final String code;

    /**
     * A short, human-readable error title.
     * Suitable for display to end users and in log entries.
     */
    private final String title;

    /**
     * Constructs an error code with HTTP status, code, and title.
     *
     * @param httpStatus the HTTP status to return to the client
     * @param code the machine-readable error identifier
     * @param title the human-readable error description
     */
    ErrorCode(HttpStatus httpStatus, String code, String title) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.title = title;
    }
}

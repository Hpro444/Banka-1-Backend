package com.banka1.verificationService.dto.response;

import com.banka1.verificationService.model.enums.VerificationStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for the response when querying a verification session's status.
 *
 * Provides the current state of a session without performing any validation.
 * Useful for polling the status of a session asynchronously.
 */
@Getter
@Setter
public class StatusResponse {
    /**
     * The unique identifier of the verification session.
     */
    private Long sessionId;

    /**
     * The current status of the verification session.
     * May be automatically updated to EXPIRED if the session's expiration time has passed.
     *
     * @see VerificationStatus
     */
    private VerificationStatus status;

    /**
     * Constructs a status response with the session ID and current status.
     *
     * @param sessionId the ID of the verification session
     * @param status the current state of the session
     */
    public StatusResponse(Long sessionId, VerificationStatus status) {
        this.sessionId = sessionId;
        this.status = status;
    }
}

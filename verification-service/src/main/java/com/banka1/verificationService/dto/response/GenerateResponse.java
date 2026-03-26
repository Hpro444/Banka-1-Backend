package com.banka1.verificationService.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for the response after generating a verification session.
 *
 * Provides the client with the session ID needed for subsequent validation requests.
 * The OTP code is never returned; it is sent separately via email by the notification service.
 */
@Getter
@Setter
public class GenerateResponse {
    /**
     * The unique identifier of the newly created verification session.
     * Client must provide this ID when submitting the verification code for validation.
     */
    private Long sessionId;

    /**
     * Constructs a response with the generated session ID.
     *
     * @param sessionId the ID of the newly created verification session
     */
    public GenerateResponse(Long sessionId) {
        this.sessionId = sessionId;
    }
}

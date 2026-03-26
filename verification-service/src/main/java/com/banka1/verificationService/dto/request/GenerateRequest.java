package com.banka1.verificationService.dto.request;

import com.banka1.verificationService.model.enums.OperationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for requesting a new verification session.
 *
 * Encapsulates all information required to generate a verification session,
 * hash an OTP code, and trigger email delivery via RabbitMQ.
 * All fields are validated upon deserialization.
 */
@Getter
@Setter
public class GenerateRequest {
    /**
     * The ID of the client requesting verification.
     * Must match the authenticated user's ID for security reasons.
     * Required and non-null.
     */
    @NotNull(message = "clientId is required.")
    private Long clientId;

    /**
     * The type of operation requiring verification.
     * Categorizes the verification session (e.g., PAYMENT, TRANSFER).
     * Required and non-null.
     *
     * @see OperationType
     */
    @NotNull(message = "operationType is required.")
    private OperationType operationType;

    /**
     * The ID of the entity associated with this verification.
     * Typically a transaction ID, request ID, or card request ID.
     * Used to ensure only one concurrent verification per (clientId, operationType, relatedEntityId) combination.
     * Required and non-empty.
     */
    @NotBlank(message = "relatedEntityId is required.")
    private String relatedEntityId;

    /**
     * The client's email address for receiving the OTP code.
     * Must be a valid email format per RFC 5322.
     * Used by the notification service to send the verification code.
     * Required and non-empty.
     */
    @NotBlank(message = "clientEmail is required.")
    @Email(message = "clientEmail must be a valid email address.")
    private String clientEmail;
}

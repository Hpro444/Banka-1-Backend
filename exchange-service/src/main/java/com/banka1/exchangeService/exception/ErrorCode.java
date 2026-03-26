package com.banka1.exchangeService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Standardized error codes for the exchange-service.
 * Each code maps to an HTTP status, a machine-readable code, and a user-friendly title.
 */
@Getter
public enum ErrorCode {
    /**
     * Indicates that a requested exchange rate was not found in the system.
     */
    EXCHANGE_RATE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_EXCHANGE_RATE_NOT_FOUND", "Exchange rate not found"),
    /**
     * Indicates that fetching exchange rates from the external provider failed.
     */
    EXCHANGE_RATE_FETCH_FAILED(HttpStatus.BAD_GATEWAY, "ERR_EXCHANGE_RATE_FETCH_FAILED", "Exchange rate fetch failed"),
    /**
     * Indicates that a provided currency code is not supported.
     */
    UNSUPPORTED_CURRENCY(HttpStatus.BAD_REQUEST, "ERR_UNSUPPORTED_CURRENCY", "Unsupported currency"),
    /**
     * Indicates a validation error in the request parameters.
     */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "ERR_VALIDATION", "Validation error");

    /**
     * HTTP status code associated with this error.
     */
    private final HttpStatus httpStatus;
    /**
     * Machine-readable error code.
     */
    private final String code;
    /**
     * User-friendly error title.
     */
    private final String title;

    /**
     * Creates an error code with the specified status, code, and title.
     *
     * @param httpStatus HTTP status code
     * @param code machine-readable error code
     * @param title user-friendly error title
     */
    ErrorCode(HttpStatus httpStatus, String code, String title) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.title = title;
    }
}

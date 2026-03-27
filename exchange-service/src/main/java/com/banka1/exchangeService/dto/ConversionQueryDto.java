package com.banka1.exchangeService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO that maps query parameters from the endpoint
 * {@code GET /calculate?fromCurrency=...&toCurrency=...&amount=...}.
 * Spring MVC automatically binds values from the URL query string to these fields
 * and then invokes Bean Validation annotations defined on them.
 */
@Getter
@Setter
public class ConversionQueryDto {

    /**
     * Regular expression pattern for validating supported currency codes.
     */
    private static final String SUPPORTED_CURRENCY_REGEX = "^(?i)(RSD|EUR|CHF|USD|GBP|JPY|CAD|AUD)$";
    /**
     * Validation error message for unsupported currencies.
     */
    private static final String SUPPORTED_CURRENCY_MESSAGE =
            "Supported currencies are RSD, EUR, CHF, USD, GBP, JPY, CAD and AUD.";

    /**
     * Source currency from which the user converts the amount.
     */
    @NotBlank(message = "fromCurrency is required.")
    @Pattern(regexp = SUPPORTED_CURRENCY_REGEX, message = SUPPORTED_CURRENCY_MESSAGE)
    @Schema(example = "EUR", allowableValues = {"RSD", "EUR", "CHF", "USD", "GBP", "JPY", "CAD", "AUD"})
    private String fromCurrency;

    /**
     * Target currency to which the amount is converted.
     */
    @NotBlank(message = "toCurrency is required.")
    @Pattern(regexp = SUPPORTED_CURRENCY_REGEX, message = SUPPORTED_CURRENCY_MESSAGE)
    @Schema(example = "USD", allowableValues = {"RSD", "EUR", "CHF", "USD", "GBP", "JPY", "CAD", "AUD"})
    private String toCurrency;

    /**
     * Amount to be converted.
     */
    @NotNull(message = "amount is required.")
    @DecimalMin(value = "0.00000001", message = "amount must be greater than 0.")
    @Schema(example = "100.00")
    private BigDecimal amount;

    /**
     * Optional rate list date.
     * If not provided, the latest available local snapshot is used.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(example = "2026-03-22")
    private LocalDate date;
}

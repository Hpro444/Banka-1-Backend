package com.banka1.transaction_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO odgovora nakon kreiranja novog plaćanja.
 * Sadrži poruku i status plaćanja.
 */
@Getter
@AllArgsConstructor
public class NewPaymentResponseDto {

    /** Odgovorna poruka za klijenta o rezultatu plaćanja */
    private String message;

    /** Status plaćanja: COMPLETED, DENIED ili IN_PROGRESS */
    private String status;
}


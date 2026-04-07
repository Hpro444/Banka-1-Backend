package com.banka1.transaction_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO odgovora sa ažuriranim stanjem na računima nakon transakcije.
 */
@Getter
@Setter
@AllArgsConstructor
public class UpdatedBalanceResponseDto {

    /** Novo stanje na računu pošiljaoca nakon transakcije */
    private BigDecimal senderBalance;

    /** Novo stanje na računu primaoca nakon transakcije */
    private BigDecimal receiverBalance;
}

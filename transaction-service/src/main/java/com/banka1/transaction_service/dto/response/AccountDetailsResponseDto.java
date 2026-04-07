package com.banka1.transaction_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO za odgovor sa detaljnim informacijama o bankarskom računu.
 * Sadrži identifikacione podatke o vlasniku računa.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsResponseDto {

    /** ID vlasnika računa (klijenta) */
    @JsonProperty("ownerId")
    private Long vlasnik;
}

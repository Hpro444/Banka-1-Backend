package com.banka1.exchangeService.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA entity representing a locally stored exchange rate in the {@code exchange_rate} table.
 * Each row corresponds to one currency for one snapshot date.
 * The combination of {@code currency_code + rate_date} must be unique to prevent
 * duplicates for the same currency and date.
 */
@Entity
@Table(
        name = "exchange_rate",
        uniqueConstraints =
        @UniqueConstraint(
                name = "uk_exchange_rate_currency_date",
                columnNames = {"currency_code", "rate_date"}
        )
)
@Getter
@Setter
public class ExchangeRateEntity {
    /**
     * Technical primary key of the row in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Three-letter ISO currency code, for example {@code EUR} or {@code USD}.
     */
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    /**
     * Rate at which the bank buys the currency from clients.
     */
    @Column(name = "buying_rate", nullable = false, precision = 19, scale = 8)
    private BigDecimal buyingRate;

    /**
     * Rate at which the bank sells the currency to clients.
     */
    @Column(name = "selling_rate", nullable = false, precision = 19, scale = 8)
    private BigDecimal sellingRate;

    /**
     * Effective date of the daily rate snapshot.
     */
    @Column(name = "rate_date", nullable = false)
    private LocalDate date;

    /**
     * Timestamp when the row was first inserted into the database.
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Automatically sets the creation timestamp on first insert.
     */
    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}

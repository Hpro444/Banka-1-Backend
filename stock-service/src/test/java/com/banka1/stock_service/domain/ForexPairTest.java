package com.banka1.stock_service.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForexPairTest {

    @Test
    void shouldReturnFixedContractSizeAndDerivedValuesFromExchangeRate() {
        ForexPair forexPair = new ForexPair();
        forexPair.setExchangeRate(new BigDecimal("1.08350"));

        assertEquals(1_000, forexPair.getContractSize());
        assertEquals(new BigDecimal("1083.50000"), forexPair.calculateNominalValue());
        assertEquals(new BigDecimal("108.3500000"), forexPair.calculateMaintenanceMargin());
    }

    @Test
    void shouldRejectNullExchangeRateWhenCalculatingDerivedValues() {
        ForexPair forexPair = new ForexPair();

        assertThrows(NullPointerException.class, forexPair::calculateNominalValue);
        assertThrows(NullPointerException.class, forexPair::calculateMaintenanceMargin);
    }
}

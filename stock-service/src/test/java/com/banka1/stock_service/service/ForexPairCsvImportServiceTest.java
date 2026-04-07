package com.banka1.stock_service.service;

import com.banka1.stock_service.config.ForexPairSeedProperties;
import com.banka1.stock_service.domain.ForexPair;
import com.banka1.stock_service.domain.Liquidity;
import com.banka1.stock_service.dto.ForexPairImportResponse;
import com.banka1.stock_service.repository.ForexPairRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ForexPairCsvImportService}.
 */
@ExtendWith(MockitoExtension.class)
class ForexPairCsvImportServiceTest {

    @Mock
    private ForexPairRepository forexPairRepository;

    @Test
    void importFromResourceCreatesNewPairsFromCsv() {
        ForexPairCsvImportService service = createService("classpath:seed/forex_pairs_seed.csv");
        when(forexPairRepository.findAllByTickerIn(any())).thenReturn(List.of());
        when(forexPairRepository.saveAll(any())).thenAnswer(invocation -> List.of());

        ForexPairImportResponse response = service.importFromResource(
                csvResource("""
                        Ticker,Base Currency,Quote Currency,Exchange Rate,Liquidity
                        EURUSD,EUR,USD,1.08350,HIGH
                        USDJPY,USD,JPY,151.25000,HIGH
                        """),
                "test-forex.csv"
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ForexPair>> captor = ArgumentCaptor.forClass(List.class);
        verify(forexPairRepository).saveAll(captor.capture());
        List<ForexPair> savedEntities = captor.getValue();

        assertThat(response.processedRows()).isEqualTo(2);
        assertThat(response.createdCount()).isEqualTo(2);
        assertThat(response.updatedCount()).isZero();
        assertThat(response.unchangedCount()).isZero();
        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities.getFirst().getTicker()).isEqualTo("EURUSD");
        assertThat(savedEntities.getFirst().getBaseCurrency()).isEqualTo("EUR");
        assertThat(savedEntities.getFirst().getQuoteCurrency()).isEqualTo("USD");
        assertThat(savedEntities.getFirst().getExchangeRate()).isEqualTo(new BigDecimal("1.08350"));
        assertThat(savedEntities.getFirst().getLiquidity()).isEqualTo(Liquidity.HIGH);
        assertThat(savedEntities.getFirst().getContractSize()).isEqualTo(1_000);
    }

    @Test
    void importFromConfiguredCsvLoadsDummySeedFileFromResources() {
        ForexPairCsvImportService service = createService("classpath:seed/forex_pairs_seed.csv");
        when(forexPairRepository.findAllByTickerIn(any())).thenReturn(List.of());
        when(forexPairRepository.saveAll(any())).thenAnswer(invocation -> List.of());

        ForexPairImportResponse response = service.importFromConfiguredCsv();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ForexPair>> captor = ArgumentCaptor.forClass(List.class);
        verify(forexPairRepository).saveAll(captor.capture());
        List<ForexPair> savedEntities = captor.getValue();

        assertThat(response.source()).isEqualTo("classpath:seed/forex_pairs_seed.csv");
        assertThat(response.processedRows()).isEqualTo(3);
        assertThat(savedEntities).extracting(ForexPair::getTicker)
                .containsExactly("EURUSD", "USDJPY", "GBPCHF");
    }

    @Test
    void importFromResourceSkipsUnchangedPairOnRepeatedImport() {
        ForexPair existingPair = new ForexPair();
        existingPair.setTicker("EURUSD");
        existingPair.setBaseCurrency("EUR");
        existingPair.setQuoteCurrency("USD");
        existingPair.setExchangeRate(new BigDecimal("1.08350"));
        existingPair.setLiquidity(Liquidity.HIGH);

        ForexPairCsvImportService service = createService("classpath:seed/forex_pairs_seed.csv");
        when(forexPairRepository.findAllByTickerIn(any())).thenReturn(List.of(existingPair));

        ForexPairImportResponse response = service.importFromResource(
                csvResource("""
                        Ticker,Base Currency,Quote Currency,Exchange Rate,Liquidity
                        EURUSD,EUR,USD,1.08350,HIGH
                        """),
                "test-forex.csv"
        );

        assertThat(response.processedRows()).isEqualTo(1);
        assertThat(response.createdCount()).isZero();
        assertThat(response.updatedCount()).isZero();
        assertThat(response.unchangedCount()).isEqualTo(1);
        verify(forexPairRepository, never()).saveAll(any());
    }

    @Test
    void importFromResourceRejectsDuplicateTickersInsideCsv() {
        ForexPairCsvImportService service = createService("classpath:seed/forex_pairs_seed.csv");

        assertThatThrownBy(() -> service.importFromResource(
                csvResource("""
                        Ticker,Base Currency,Quote Currency,Exchange Rate,Liquidity
                        EURUSD,EUR,USD,1.08350,HIGH
                        EURUSD,EUR,USD,1.09000,MEDIUM
                        """),
                "test-forex.csv"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate FX ticker 'EURUSD'");
    }

    private ForexPairCsvImportService createService(String csvLocation) {
        return new ForexPairCsvImportService(
                forexPairRepository,
                new ForexPairSeedProperties(true, csvLocation),
                new DefaultResourceLoader()
        );
    }

    private ByteArrayResource csvResource(String content) {
        return new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
    }
}

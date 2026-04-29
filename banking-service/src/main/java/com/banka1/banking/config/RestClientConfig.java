package com.banka1.banking.config;

import com.banka1.account_service.rest_client.JwtAuthInterceptor;
import com.banka1.account_service.security.JWTService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Consolidated REST client configuration for all external service calls.
 * Replaces individual RestClientConfig classes from account, credit, transaction and transfer services.
 * Account-service calls are NOT included here — they are now inner Java calls.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    // --- Beans used by account-service ---

    @Bean("userRestClient")
    public RestClient userRestClient(
            RestClient.Builder builder,
            @Value("${services.user.url}") String baseUrl,
            JWTService jwtService
    ) {
        return builder.clone()
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    @Bean("cardRestClient")
    public RestClient cardRestClient(
            @Value("${services.card.url}") String baseUrl,
            JWTService jwtService
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    // --- Beans used by credit-service and transaction-service ---

    @Bean("userClient")
    public RestClient userClient(
            @Value("${services.user.url}") String baseUrl,
            JWTService jwtService
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    @Bean("exchangeClient")
    public RestClient exchangeClient(
            @Value("${services.exchange.url}") String baseUrl,
            JWTService jwtService
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    // --- Beans used by multiple services (account, credit, transaction) ---

    @Bean("verificationClient")
    public RestClient verificationClient(
            RestClient.Builder builder,
            @Value("${services.verification.url}") String baseUrl,
            JWTService jwtService
    ) {
        return builder.clone()
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    // --- Beans used by transfer-service (field-name injection) ---

    @Bean("exchangeRestClient")
    public RestClient exchangeRestClient(
            @Value("${services.exchange.url}") String baseUrl,
            JWTService jwtService
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    @Bean("verificationRestClient")
    public RestClient verificationRestClient(
            @Value("${services.verification.url}") String baseUrl,
            JWTService jwtService
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    @Bean("clientRestClient")
    public RestClient clientRestClient(
            @Value("${services.client.url}") String baseUrl,
            JWTService jwtService
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }
}

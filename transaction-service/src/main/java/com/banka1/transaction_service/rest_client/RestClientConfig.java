package com.banka1.transaction_service.rest_client;

import com.banka1.transaction_service.security.JWTService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Spring konfiguracija REST klijenta za komunikaciju sa ostalim mikservisima.
 * Konfigurira RestClient bean-ove sa JWT autentifikacijom za različite servise.
 */
@Configuration
public class RestClientConfig {

    /**
     * Kreira RestClient builder bean.
     *
     * @return RestClient builder
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Kreira REST klijent za User/Client Service sa JWT autentifikacijom.
     *
     * @param builder RestClient builder
     * @param baseUrl URL baznog servisa iz konfiguracije
     * @param jwtService servis za generisanje JWT tokena
     * @return konfigurisan REST klijent
     */
    @Bean
    public RestClient userClient(
            RestClient.Builder builder,
            @Value("${services.user.url}") String baseUrl,
            JWTService jwtService
    ) {
        return builder
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    /**
     * Kreira REST klijent za Verification Service sa JWT autentifikacijom.
     *
     * @param builder RestClient builder
     * @param baseUrl URL baznog servisa iz konfiguracije
     * @param jwtService servis za generisanje JWT tokena
     * @return konfigurisan REST klijent
     */
    @Bean
    public RestClient verificationClient(
            RestClient.Builder builder,
            @Value("${services.verification.url}") String baseUrl,
            JWTService jwtService
    ) {
        return builder
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    /**
     * Kreira REST klijent za Exchange Service sa JWT autentifikacijom.
     *
     * @param builder RestClient builder
     * @param baseUrl URL baznog servisa iz konfiguracije
     * @param jwtService servis za generisanje JWT tokena
     * @return konfigurisan REST klijent
     */
    @Bean
    public RestClient exchangeClient(
            RestClient.Builder builder,
            @Value("${services.exchange.url}") String baseUrl,
            JWTService jwtService
    ) {
        return builder
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }

    /**
     * Kreira REST klijent za Account Service sa JWT autentifikacijom.
     *
     * @param builder RestClient builder
     * @param baseUrl URL baznog servisa iz konfiguracije
     * @param jwtService servis za generisanje JWT tokena
     * @return konfigurisan REST klijent
     */
    @Bean
    public RestClient accountClient(
            RestClient.Builder builder,
            @Value("${services.account.url}") String baseUrl,
            JWTService jwtService
    ) {
        return builder
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor(jwtService))
                .build();
    }


}

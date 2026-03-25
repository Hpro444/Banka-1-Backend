package com.banka1.transfer.client.impl;

import com.banka1.transfer.client.VerificationClient;
import com.banka1.transfer.dto.client.VerificationResponseDto;
import com.banka1.transfer.dto.client.VerificationValidateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Implementacija klijenta za verifikaciju putem POST zahteva.
 */
@Component
@Profile("!local")
@RequiredArgsConstructor
public class VerificationClientImpl implements VerificationClient {

    private final RestClient verificationRestClient;

    @Override
    public VerificationResponseDto validateCode(String sessionId, String code) {
        return verificationRestClient.post()
                .uri("/api/verification/validate")
                .body(new VerificationValidateRequestDto(sessionId, code))
                .retrieve()
                .body(VerificationResponseDto.class);
    }
}

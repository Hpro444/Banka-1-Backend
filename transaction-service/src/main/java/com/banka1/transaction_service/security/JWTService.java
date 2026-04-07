package com.banka1.transaction_service.security;

/**
 * Interfejs za servis koji generiše JWT tokene za autentifikaciju.
 * Koristi se za komunikaciju između mikservisima.
 */
public interface JWTService {

    /**
     * Generiše JWT pristupni token sa standardnim claim-ima.
     *
     * @return serijalizovan potpisani JWT token kao string
     */
    String generateJwtToken();


}

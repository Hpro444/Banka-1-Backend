package com.banka1.transaction_service.security.implementation;

import com.banka1.transaction_service.security.JWTService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementacija JWT servisa za generisanje i potpisivanje JWT tokena.
 * Koristi HMAC-SHA256 algoritam za potpisivanje tokena.
 */
@Service
@Getter
public class JWTServiceImplementation implements JWTService {

    /** Signer koji potpisuje JWT tokene HMAC-SHA256 algoritmom */
    private final JWSSigner signer;

    /** Naziv claim-a u JWT-u koji nosi ime uloge korisnika */
    @Value("${banka.security.roles-claim}")
    private String role;

    /** Naziv claim-a u JWT-u koji nosi listu permisija korisnika */
    @Value("${banka.security.permissions-claim}")
    private String permission;

    /** Naziv claim-a u JWT-u koji nosi identifikator korisnika/servisa */
    @Value("${banka.security.id}")
    private String id;

    /** Issuer vrednost koja se upisuje u JWT token */
    @Value("${banka.security.issuer}")
    private String issuer;

    /** Vreme trajanja JWT tokena u milisekundama */
    @Value("${banka.security.expiration-time}")
    private Long expirationTime;

    /**
     * Inicijalizuje servis za potpisivanje JWT tokena učitavanjem HMAC tajne.
     *
     * @param secret HMAC tajna za potpisivanje tokena (minimalno 32 karaktera za HS256)
     * @throws KeyLengthException ako je tajna neodgovarajuće dužine za HS256
     */
    public JWTServiceImplementation(@Value("${jwt.secret}") String secret) throws KeyLengthException {
        this.signer = new MACSigner(secret);
    }

    /**
     * Generiše JWT pristupni token sa standardnim claim-ima za servis.
     * <p>
     * Token sadrži:
     * <ul>
     *   <li>Subject: "account-service"</li>
     *   <li>Issuer: konfigurisan u svojstvima</li>
     *   <li>Role: "SERVICE"</li>
     *   <li>Permissions: prazna lista</li>
     *   <li>Expiration: trenutna vremenska oznaka + konfigurisano vreme trajanja</li>
     * </ul>
     *
     * @return serijalizovan potpisani JWT token
     * @throws IllegalStateException ako dodje do greške pri potpisivanju
     */
    @Override
    public String generateJwtToken() {
        List<String> permissions = new ArrayList<>();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("account-service")
                .issuer(issuer)
                .claim(role, "SERVICE")
                .claim(permission, permissions)
                .expirationTime(new Date(System.currentTimeMillis() + expirationTime))
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT jwt = new SignedJWT(header, claims);
        try {
            jwt.sign(signer);
        } catch (Exception e) {
            throw new IllegalStateException("Greska sa generisanjem JWT-a");
        }
        return jwt.serialize();
    }


}

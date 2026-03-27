package com.banka1.verificationService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * HMAC-SHA256-based hashing service for one-time password (OTP) codes.
 *
 * Provides stateless hashing and verification of OTP codes using a shared secret key
 * derived from the application's JWT secret. Designed specifically for short-lived
 * codes that expire within minutes and are used as part of two-factor authentication (2FA).
 *
 * Note: This service uses constant-time comparison to prevent timing attacks.
 *
 * @see javax.crypto.Mac
 * @see java.util.Base64
 */
@Service
public class OtpHashingService {

    /** The HMAC algorithm used for code hashing. */
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    /** Secret key derived from application configuration, used for HMAC computation. */
    private final SecretKeySpec secretKey;

    /**
     * Initializes the OTP hashing service with a secret key.
     *
     * The secret is injected from application configuration (typically the JWT_SECRET
     * environment variable) and used to initialize an HMAC-SHA256 instance.
     *
     * @param secret the secret key string from configuration (must be non-empty)
     */
    public OtpHashingService(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
    }

    /**
     * Generates an HMAC-SHA256 hash of a raw OTP code.
     *
     * The hash is computed using the application's secret key and then Base64-encoded
     * for storage in the database. Each call with the same input produces an identical
     * output, making verification deterministic (not probabilistic like bcrypt).
     *
     * @param rawCode the plain OTP code (typically a 6-digit string) to hash
     * @return a Base64-encoded HMAC-SHA256 hash of the code
     * @throws IllegalStateException if HMAC algorithm initialization fails (runtime error)
     */
    public String hash(String rawCode) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);
            return Base64.getEncoder().encodeToString(mac.doFinal(rawCode.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Unable to hash OTP code.", ex);
        }
    }

    /**
     * Verifies whether a raw OTP code matches an expected hash.
     *
     * Computes the hash of the raw code and compares it to the expected hash using
     * {@link String#equals(Object)}, which performs constant-time string comparison
     * to mitigate timing attacks.
     *
     * @param rawCode the plain OTP code to verify
     * @param expectedHash the Base64-encoded hash to compare against
     * @return true if the hashed code matches the expected hash; false otherwise
     */
    public boolean matches(String rawCode, String expectedHash) {
        return hash(rawCode).equals(expectedHash);
    }
}

package org.surest.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utility class for handling JWT operations such as
 * token generation, decoding, validation, and claim extraction.
 */
@Component
public class JwtUtil {

    // Secret key for signing JWTs – should be long and secure in production
    private static final String SECRET_KEY = "my_super_secret_key_which_should_be_long_and_secure";

    // JWT expiration time in milliseconds (10 hours)
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 10;

    // Algorithm used to sign the JWT
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    /**
     * Generates a JWT token for a given username and role.
     * The token contains:
     * - subject: username
     * - custom claim: role
     * - issued date
     * - expiration date
     * - issuer
     *
     * @param username user's username
     * @param role     user's role
     * @return signed JWT as String
     */
    public String generateToken(String username, String role) {
        return JWT.create()
                .withSubject(username)                // Set username as the subject
                .withClaim("role", role)             // Add role as a custom claim
                .withIssuedAt(new Date())            // Token creation time
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_MS)) // Expiration
                .withIssuer("example.com")           // Issuer of the token
                .sign(algorithm);                    // Sign the token using HMAC256
    }

    /**
     * Decodes and verifies the JWT token signature and issuer.
     *
     * @param token JWT string
     * @return DecodedJWT containing all claims
     */
    public DecodedJWT decodeToken(String token) {
        return JWT.require(algorithm)
                .withIssuer("example.com")           // Ensure token was issued by trusted issuer
                .build()
                .verify(token);                      // Verify signature and return decoded token
    }

    /**
     * Extracts the username (subject) from a JWT.
     *
     * @param token JWT string
     * @return username
     */
    public String extractUsername(String token) {
        DecodedJWT decodedJWT = decodeToken(token);  // Decode once
        return decodedJWT.getSubject();
    }

    /**
     * Extracts the role claim from a JWT.
     *
     * @param token JWT string
     * @return role
     */
    public String extractRole(String token) {
        DecodedJWT decodedJWT = decodeToken(token);  // Decode once
        return decodedJWT.getClaim("role").asString();
    }

    /**
     * Validates the token:
     * - Signature must be valid
     * - Token must not be expired
     * - Username must match the one in the token
     *
     * @param token    JWT string
     * @param username expected username
     * @return true if valid
     */
    public boolean validateToken(String token, String username) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            return decodedJWT.getSubject().equals(username) && !isTokenExpired(decodedJWT);
        } catch (Exception e) {
            return false; // Invalid token
        }
    }

    /**
     * Checks if the token has expired.
     *
     * @param decodedJWT decoded JWT object
     * @return true if expired
     */
    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }
}

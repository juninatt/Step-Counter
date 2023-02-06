package com.nexergroup.boostapp.java.step.controller;

import org.springframework.security.oauth2.jwt.Jwt;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A converter that extracts the user ID from a JWT (JSON Web Token) and returns it as a string.
 * If the JWT is invalid or the user ID claim is not present, a default value is returned.
 *
 */
public class JwtValidator {

    private static final String OID_CLAIM = "oid";
    private static final String DEFAULT_VALUE = "";

    /**
     * Converts the given JWT to a user ID string.
     *
     * @param jwt the JWT to be converted
     * @return the user ID if the JWT is valid, DEFAULT_VALUE otherwise
     */
    public static String getUserId(@NotNull Jwt jwt) {
        return jwtIsValid(jwt) ?
                extractUserIdFromJwtClaim(jwt) :
                DEFAULT_VALUE;
    }

    /**
     * Extracts the user ID from the given JWT's claims.
     *
     * @param jwt the JWT from which to extract the user ID
     * @return the user ID as a String, or the DEFAULT_VALUE if the user ID could not be extracted or is not a String
     */
    private static String extractUserIdFromJwtClaim(Jwt jwt) {
        return Optional.ofNullable(jwt)
                .map(Jwt::getClaims)
                .map(c -> c.get(OID_CLAIM))
                .filter(userId -> userId instanceof String)
                .map(userId -> (String) userId)
                .orElse(DEFAULT_VALUE);
    }

    /**
     * Validates the given JWT.
     * A JWT is considered valid if it is not null and has a non-null claims object, and the expiration date
     * is not before the current time and the issued-at date is not after the current time.
     *
     * @param jwt the JWT to validate
     * @return true if the JWT is valid, false otherwise
     */
    private static boolean jwtIsValid(Jwt jwt) {
        try {
            var now = Instant.now();
            return !Objects.requireNonNull(jwt.getExpiresAt()).isBefore(now) && !Objects.requireNonNull(jwt.getIssuedAt()).isAfter(now);
        }
        catch (NullPointerException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}

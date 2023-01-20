package se.sigma.boostapp.boost_app_java.controller;

import org.junit.jupiter.api.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;

@DisplayName(" <== JwtValidator Test ==>")
public class JwtValidatorTest {
    String tokenValue = "JWT";
    Instant thisInstant = Instant.now();
    HashMap<String, Object> headers = new HashMap<>();
    HashMap<String, Object> claims = new HashMap<>();

    private final static String DEFAULT_VALUE = "";


    @Nested
    @DisplayName("Convert method should return: ")
    class ParserShouldReturn {


        @BeforeEach
        void setParser() {
            headers.put("alg", "HS526");
            claims.put("oid", "testUser");
        }

        @Test
        @DisplayName("chosen default value when input is null")
        void shouldReturnStringWhenInputIsNull() {
            var actual = JwtValidator.getUserId(null);

            Assertions.assertEquals(String.class, actual.getClass());
        }

        @Test
        @DisplayName("user id as string when input is valid")
        void shouldReturnUserIdAsStringWhenInputIsValid() {
            var jwt = new Jwt(tokenValue, thisInstant, thisInstant.plusSeconds(60), headers, claims);

            var expected = "testUser";

            var actual = JwtValidator.getUserId(jwt);

            Assertions.assertEquals(expected, actual);
        }

        @Test
        @DisplayName("chosen default value when JWT's expiration date has passed")
        void shouldReturnDefaultValueWhenExpirationDateHasPassed() {
            var expiredJwt = new Jwt(tokenValue, thisInstant.minusSeconds(60), thisInstant.minusSeconds(30), headers, claims);

            var actual = JwtValidator.getUserId(expiredJwt);

            Assertions.assertEquals(DEFAULT_VALUE, actual);
        }

        @Test
        @DisplayName("chosen default value when oid claim is not present in JWT's claims")
        void shouldReturnDefaultValueWhenOidClaimIsNotPresent() {
            var emptyClaims = new HashMap<String, Object>();
            emptyClaims.put("oid", null);
            var jwtWithNoOidClaim = new Jwt(tokenValue, thisInstant, thisInstant.plusSeconds(30), headers, emptyClaims);

            var actual = JwtValidator.getUserId(jwtWithNoOidClaim);

            Assertions.assertEquals(DEFAULT_VALUE, actual);
        }

        @Test
        @DisplayName("chosen default value when oid claim is not of type String in JWT's claims")
        void shouldReturnDefaultValueWhenOidClaimIsNotString() {
            var claims = new HashMap<String, Object>();
            claims.put("oid", 123);
            var jwtWithInvalidOidClaim = new Jwt(tokenValue, thisInstant, thisInstant.plusSeconds(30), headers, claims);

            var actual = JwtValidator.getUserId(jwtWithInvalidOidClaim);

            Assertions.assertEquals(DEFAULT_VALUE, actual);
        }
    }
}

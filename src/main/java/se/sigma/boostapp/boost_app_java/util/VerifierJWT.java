package se.sigma.boostapp.boost_app_java.util;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
@PropertySource("classpath:jwt.properties")
public class VerifierJWT {
	
	@Value("${jwt.secret}")
	private String secret;
	
	public <T> T verifyJwtToken(final Supplier<T> success, final Supplier<T> error, String token) {
        try {
            final Algorithm algorithm = Algorithm.HMAC256(secret);
            final JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build(); //Reusable verifier instance
            final DecodedJWT jwt = verifier.verify(token);
            return success.get();
        } catch (final JWTVerificationException exception){
            //Invalid signature/claims
            return error.get();
        }
    }
}

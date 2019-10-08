package se.sigma.boostapp.boost_app_java.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class JWTFilter implements Filter {

	@Value("${jwt.secret}")
	private String secret;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		if (httpRequest.getHeader("Authorization") == null) {
			httpResponse.setStatus(403);
		} else {
			String token = httpRequest.getHeader("Authorization");
			try {
				Algorithm algorithm = Algorithm.HMAC256(secret);
				JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); // Reusable verifier instance
				DecodedJWT jwt = verifier.verify(token);
				httpResponse.setStatus(200);
				chain.doFilter(request, response);
			} catch (JWTVerificationException exception) {
				httpResponse.setStatus(401);
			}

		}
	}

}

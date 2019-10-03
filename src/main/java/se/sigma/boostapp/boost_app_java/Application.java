package se.sigma.boostapp.boost_app_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
		// For testing of JSON web token
		try {
		    Algorithm algorithm = Algorithm.HMAC256("password");
		    String token = JWT.create()
		        .withIssuer("auth0")
		        .sign(algorithm);
		    System.out.println(token);
		} catch (JWTCreationException exception){
		    //Invalid Signing configuration / Couldn't convert Claims.
		}
	}

}

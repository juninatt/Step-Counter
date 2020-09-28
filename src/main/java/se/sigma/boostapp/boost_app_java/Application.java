package se.sigma.boostapp.boost_app_java;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;



/*
 * javadoc syntaks
 * 
 * @author (classes and interfaces only, required)
@version (classes and interfaces only, required. See footnote 1)
@param (methods and constructors only)
@return (methods only)
@exception (@throws is a synonym added in Javadoc 1.2)
@see
@since
@serial (or @serialField or @serialData)
@deprecated (see How and When To Deprecate APIs)
 * 
 * 
 */
/**
 * @author SigmaIT
 * 
 * <br> Start application and start scheduling( deleting table step on the Monday 00:00:01 )
 */



@SpringBootApplication
@Configuration
@EnableScheduling
@ConditionalOnProperty(name="deleting.enabled", matchIfMissing=true)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
	}
}

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


@SpringBootApplication
@Configuration
@EnableScheduling
@ConditionalOnProperty(name="deleting.enabled", matchIfMissing=true)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
	}
}

package com.nexergroup.boostapp.java.step;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 *Start application and start scheduling( deleting table step on the Monday 00:00:01 )
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

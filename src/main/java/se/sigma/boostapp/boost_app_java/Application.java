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
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
	}
	
	// delete data from table step on the Monday: 00:00:01
	@SuppressWarnings("null")
	//@Scheduled (cron="1 23 11 * * SUN")
	//1=secund , 0=minut, 0= hours, *-dayOfTheMonth *-month MON-Monday
	@Scheduled (cron="1 0 0 * * MON")
	void deleteTablePerDagOnMonday() throws InterruptedException{
		
		//TESTING method:system.out is working
		System.out.println(LocalDateTime.now());
	
		String url="jdbc:postgresql://localhost:5432/postgres";
			String user="postgres";
			String password="postgres";
			try(Connection con=DriverManager.getConnection(url, user, password);
					Statement st=con.createStatement()){
				st.execute("DELETE FROM step where id>0");
				st.close();		
			} catch (SQLException e) {
				System.out.println("An error occurred");
				e.printStackTrace();
			}
	}
}

@Configuration
@EnableScheduling
// to not affect test
@ConditionalOnProperty(name="deleting.enabled", matchIfMissing=true)
class deleteTablePerDag{
	
}
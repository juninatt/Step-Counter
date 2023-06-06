# Step-Counter
### Application:
Web application to store step data from Android & iOS phones. 
Step data are sent from the phone to this service and is persisted in a database.

### Technologies:
* Maven 3.7.0
* Spring Boot v2.1.6
* Spring Security 
* Docker 
* Liquibase v3.6.3
* Swagger2 v2.8.0 

### To run locally:
Requirements:
* PostgreSQL with a database: **postgres** (user and password: **postgres**)
* Maven
* JDK 11

To Run:<br>

"mvn org.springframework.boot:spring-boot-maven-plugin:run -Pdev"

To view the API documentation in swagger when server is running, 
go to [http://localhost:8080/swagger](http://localhost:8080/swagger) in your browser.
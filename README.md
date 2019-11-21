# Boost-app-step
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

`mvn springboot:run -Pdev`

To view the API documentation in swagger when server is running, 
go to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your browser.

### To run prod enviroment:

To Run:<br>

`mvn springboot:run`

Data will regist in Azure SQL Database
You can find connection **url** and **password** to Azure SQL database in application-prod.properties

To view the API documentation in swagger when server is running, 
go to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your browser.

### Notes:
* This application validate JWT's from Sigma's Azure Active Directory
* Validation of JWT is done class webSecurityConfig
* Issuer of JWT is stored in application.properties
* JWT is also used in StepController to read the oid-key in the token to store as User ID in the database
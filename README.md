# Step-Counter

### Description:
Step-Counter is a web application designed to store step data from Android and iOS phones. 
This service receives step data from the phones and persists it in a database.

### Technologies:
The Step-Counter application utilizes the following technologies:

* Maven 3.7.0
* Spring Boot v2.1.6.RELEASE
  * Spring Security
* Liquibase v3.6.3
* OpenAPI UI (Swagger) v1.5.8
* Mapstruct v1.4.1.Final
* Guava v28.2-jre
* Junit v5.9.1

### Prerequisites

Before running the Step-Counter application locally, make sure you have the following software installed:

* PostgreSQL with a database named postgres (username and password: postgres) for "dev" profile
* Microsoft SQL Server for "prod" profile 
* Maven
* JDK 11

### Running the Application Locally

To run the Step-Counter application locally, follow these steps:

1. Open a terminal or command prompt.
2. Navigate to the project directory.
3. Execute the following command:


    mvn org.springframework.boot:spring-boot-maven-plugin:run -Pdev

This command will build and run the application using the Maven Spring Boot plugin with the "dev" profile.

4. Once the application is running, you can access the API documentation using Swagger. Open your web browser and go to http://localhost:8080/swagger.
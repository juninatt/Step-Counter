# sigma-boost-app-back-end-java
## Sigma Boost App service back end

### To run locally without docker container:
to run:
`mvn springboot:run`

### To run in docker container:
(creates a container with the app as well as a container with PostgreSQL and binds them together with docker compose)
In src/main/resources/application.properties:
  * uncomment _spring.datasource.url=jdbc:postgresql://postgres/_
  * comment out _spring.datasources.url=jdbc:postgresql://localhost:5432/postgres_ 
  
```
mvn install
mvn dockerfile:build
docker-compose up
```
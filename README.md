# sigma-boost-app-back-end-java
## Sigma Boost App service back end
### To run locally without docker container:
In src/main/resources/application.properties:
  * comment out _spring.datasource.url=jdbc:postgresql://postgres/_
  * uncomment _spring.datasources.url=jdbc:postgresql://localhost:5432/postgres_

to run:
`mvn springboot:run`

### To run in docker container:
(creates a container with the app as well as a container with PostgreSQL and binds them together with docker compose)

```
mvn install
mvn dockerfile:build
docker-compose up
```
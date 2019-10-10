# sigma-boost-app-back-end-java
## Sigma Boost App service back end

### To run locally without docker container:
to run:
`mvn springboot:run`

### To run in docker container:
(creates a container with the app as well as a container with PostgreSQL and binds them together with docker compose)
  
```
mvn install -Pprod
mvn dockerfile:build
docker-compose up
```
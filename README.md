# quarkus-doctor-label project

This project was developed in response to an assignment for a company's recruitment process.

This project is built with Quarkus. It contains all the requirements but it can be improved with event-driven comunnication and with more validations to endpoints.

## Pre-requisitos 📋
* Any IDE you feel comfortable with (eg. Intellij IDEA, Eclipse IDE, VS Code..)
* JDK 11
* Maven 3.6.x
* Docker
* PostgreSQL

## Repository structure

- **doctor-label**
  - **client**: postman related files
  - **infrastructure**: files which defines all the needed Docker images with database initialization
  - **rest-doctor-labelling**: Microservice Doctor Labelling
  - **rest-label**: Microservice Label
- **doctorlabelhelpfiles**: files provided to fill test database

## Packaging and running the application in dev mode (with test data) 🔧📦

You can run every microservice in dev mode following next steps:

**1- Run Docker Containers:**
```
cd /doctor-label/infrastructure
docker-compose -f docker-compose.yaml up -d
```
Note: if you use Linux you have to use **docker-compose-linux.yaml** or **docker-compose-linux-cuba.yaml** if you need to use VPN.

**2- Package Label Microservice:**
```
cd /doctor-label/rest-label
./mvnw package
```

**3- Run tests cases (if doesn't run in package step):**
```
./mvnw test
```
Note: The tests are completely independent. There is a configuration that deploys a container with a database and insert test data.

**4- Run Label Microservice:**
```
./mvnw quarkus:dev -Ddebug=5006
```
Note: **-Ddebug=5006** is to change the default debug port (5005) in quarkus to avoid conflict with the other mcroservice trying to listen the same port.

**5- Package Doctor Labelling Microservice:**
```
cd /doctor-label/rest-doctor-labelling
./mvnw package
```

**6- Run tests cases (if doesn't run in package step):**
```
./mvnw test
```

**7- Run Label Microservice:**
```
./mvnw quarkus:dev -Ddebug=5007
```

**8- To stop infrastructure:**
```
cd /doctor-label/infrastructure
docker-compose -f docker-compose-linux.yaml down
```

## Packaging and running the services 🔧📦

The services also can be packaged using `./mvnw package`.
It produces the `<service>-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/<service>-1.0-SNAPSHOT-runner.jar`.

## Testing with the client (Postman)
Inside the **client** folder there are 1 postman collection and one environment that stores host variables and can be modified if the proyect it's going to be deployed in other environment.

1- Go to Postman and import the collection and the environment.

2- You can start testing the endpoints (remember replace the variables {x} with the values that you want to test)

## Documentation 📋
With the APIs running you can download OpenAPI specification making a call to:
* http://localhost:8083/openapi (For Label API)
* http://localhost:8082/openapi (For Labelling API)

Or consult SwaggerUI with any web browser:
* http://localhost:8083/swagger-ui (For Label API)
* http://localhost:8082/swagger-ui (For Labelling API)

## Build with 🛠️

* [Quarkus](https://quarkus.io/) - SUPERSONIC/ SUBATOMIC/ JAVA Framework
* [Maven](https://maven.apache.org/) - Dependency managment
* [PostgreSQL](https://www.postgresql.org/) - Database
* [Postman](https://www.postman.com/) - Postman is a collaboration platform for API development
* [OpenAPI](https://swagger.io/specification/) - standard, language-agnostic interface to RESTful APIs

## Author ✒️

* **Jonad García San Martín** - *Development and Documentation* - [jgarciasm89](jgarciasm89@gmail.com)

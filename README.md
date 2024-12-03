# Tree Challenge

## Build
This project is built using Gradle. A gradle wrapper is included.

To build the project, run the following command
```shell
./gradlew build
```
The JOOQ-Sources are already generated and checked in for convenience.

## Run
To run the project, first start the database:
```shell
docker-compose up
```
The schema is automatically created on startup.

Then start the application:
```shell
./gradlew bootRun
```
The application will be served on port 8080.

## Postman
The postman collection can be found under [postman_collection.json](postman_collection.json).
It contains all three endpoints.

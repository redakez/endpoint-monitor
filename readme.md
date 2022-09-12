# :desktop_computer: Endpoint Monitoring Service

This repository contains a microservice which periodically sends GET requests to given URLs and saves responses into a MySQL database.
The results may then be retrieved using the application's REST API. 
The application is implemented in Java 11 and the Spring Boot framework. 

# :wrench: How to build and run

## Method 1: :whale: Let Docker handle everything

Install [Docker](https://www.docker.com) and then execute the following command in the root of the repository:

```
docker-compose up --build
```

Docker will run both the microservice and a MySQL database.
The microservice will be running on port specified in the variable `SPRING_LOCAL_PORT` in the `.env` file.

## Method 2: :hammer: Using maven and your own MySQL database

This method requires Java 11 and Maven 3 or newer.
First, set the following propreties in the `src/main/resources/application.properties` file:  

- `spring.datasource.url` - location of your MySQL database
- `spring.datasource.username` - username used to login into the database
- `spring.datasource.password` - password used to login into the database

Then, run the following command in the root of the repository:

```
mvn clean package
```

Assuming your MySQL database is running, execute the following command to start the microservice:

```
java -jar ./target/endpoint-monitor-1.0.jar
```

The application will be running on port 8080.

# :keyboard: Using the application

The application has no graphical user interface and only provides a REST API.
Requests accepted by the API are further specified below.

## :electric_plug: Application's endpoints

### Users :adult:

User management accessable under the `/users` path is currently **not secured in any way and requires no authentication**.

| Method |     Path      |                         Description                         |
|:------:|:-------------:|:-----------------------------------------------------------:|
|  GET   |   `/users`    |              Returns all users in the system.               |
|  GET   | `/users/{id}` |            Returns a user with the specified id.            |
|  POST  |   `/users`    |          Creates a new user. Requires a JSON body.          |
|  PUT   | `/users/{id}` | Updates a user with the specified id. Requires a JSON body. |
| DELETE | `/users/{id}` |            Deletes a user with the specified id.            |

The following is an example of a JSON body containing a user object:

```json
{
  "name": "Superman",
  "email": "superman@gmail.com",
  "accessToken": "93f39e2f-80de-4033-99ee-249d92736a25"
}
```

The `accessToken` must be in a UUID4 format.

### Monitored endpoints :signal_strength:

Each request to the `/endpoints` path requires user's access token in the header, specifically under the key `accessToken`.
Only the user who has created an endpoint monitor can access, modify, and delete it.

| Method |               Path                |                                             Description                                             |
|:------:|:---------------------------------:|:---------------------------------------------------------------------------------------------------:|
|  GET   |           `/endpoints`            |                             Returns all endpoints which the user owns.                              |
|  GET   |         `/endpoints/{id}`         |                             Returns an endpoint with the specified id.                              |
|  GET   |     `/endpoints/{id}/results`     |                       Returns all results of endpoint with the specified id.                        |
|  GET   | `/endpoints/{id}/results?count=N` |                    Returns the last N results of endpoint with the specified id.                    |
|  POST  |           `/endpoints`            | Creates a new endpoint. Requires a JSON body. User specified by the access token will be its owner. |
|  PUT   |         `/endpoints/{id}`         |                  Updates an endpoint with the specified id. Requires a JSON body.                   |
| DELETE |         `/endpoints/{id}`         |                             Deletes an endpoint with the specified id.                              |

The following is an example of a JSON body containing a monitored endpoint object:

```json
{
  "name": "Google",
  "url": "http://google.com",
  "monitoringInterval": 600
}
```

The specified url is checked at the creation of the monitored endpoint and then every `monitoringInterval` seconds (specified in the JSON body).
If the application is restarted then the url is checked at application's startup and then every `monitoringInterval` seconds.

The following is an example of a JSON body containing a monitoring result:

```json
{
  "id": 1,
  "timeOfCheck": "2017-01-24 12:28:46",
  "returnedHttpCode": 200,
  "returnedPayload": "Example of the resturned payload"
}
```

## :postbox: Postman collection 

A [Postman](https://www.postman.com/) collection with all above-mentioned API endpoints is available in file `endpoint_monitoring.postman_collection.json`.

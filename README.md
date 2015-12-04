# Vert.x demo

Demo of realtime Vert.x 3 application with REST API, worker nodes and client notifications through Websockets.

The example task is the factorization of any positive integer.

## Running the server 

`./gradlew run`

## Running the client

`node client.js`

## Querying tasks

`curl http://localhost:8080/api/tasks`

## Creating tasks

`curl --data '{"number": 123}' http://localhost:8080/api/tasks`

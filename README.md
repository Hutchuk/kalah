# Kalah

Implementation of the game Kalah, allowing two players to take turns playing via a restful service.

Instructions for the game rules can be found at https://en.wikipedia.org/wiki/Kalah.

In this implementation, for the sake of simplicity player one is the starting player so please bear that in mind when creating the game.

## Getting Started

Please follow the below instructions to get started.

### Prerequisites

[Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
[Apache Maven](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### Installing

If you have been provided with a compiled .jar file, named something like *kalah-x.x Release.jar* you can skip the next step, otherwise:

Unzip the source code to your chosen directory  or retrieve from github via *__git clone https://github.com/Hutchuk/kalah.git__*
At the command prompt type *__mvn clean install__*. This will build the project and place a compiled .jar file into the /target directiory.  I recommend this step as it will also generate some useful test documentation.
Navigate to that directory and type *__java -jar "generated jarfile name"__*.
Alternatively you can navigate to the base directory (which contains the pom.xml file) and type: __*mvn spring-boot:run*__.

### When the application is Started
The endpoints available are as follows:

| Name | Endpoint |
| :---: | :---: |
| persons API   | http://localhost:8090/api/v1/persons|
| game API      | http://localhost:8090/api/v1/kalah |


In order to create a game you must have first created two players (via post request to /persons/create) in order to pass their id's in the initial create game request.  

Once the players have been created you can go ahead and make the game via /kalah/create.  You will only need to give the ID of player one and player two.  Bear in mind whoever is created as player one will go first.

When the game is created in order to make you make a post request to /kalah/move passing the unique identifier for the game you want to make a move on, the identifier of the player making the move and the pit that player is moving from.

If you want to find a game for a specific player you can use the endpoint /kalah/playerId/{playerId} which will return a list of games that player is or has participated in.

If you used *mvn clean install* then you can navigate to */target/generated-docs* where you will find the file index.html which is automatically generated as part of the maven install process (as part of mvn package).  This details the endpoints available as well as providing sample requests and responses in addition to descriptions of the request and response fields.

Inside the project folder, navigate to \src\main\resources\Postman in order to find a postman environment and collection to import.  This provides sample requests for each of the services available.

### Built With

* [Maven](https://maven.apache.org/) - Dependency Management

### Authors

* **Neil Hutchings** - [HutchUk](mailto:hutchuk@outlook.com)




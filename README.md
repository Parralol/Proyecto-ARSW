# Proyecto-ARSW

This program allows for multiple players to play a mini version of Space Invaders!

## Architecture 

![Proyecto ARSW](https://github.com/user-attachments/assets/707cb235-e871-4b3d-adce-33666b51c111)


First of all, the given diagram shows a set of users that connect via browser to a HTTPS connection in the 8080 port, this delivers a react-js based client in which the comunication to the server will be stablished, this client consists of a App react-dom which integrates the InvadersGame class, this consists of a set of entities, players, id's, playerNames and scores, this InvadersGame is in charge of loading and using the other programm classes, this being the useLoadImages class (this class uses a resources folder), useCanvas class (class use for drawing the corresponding server output) and a useWebSocket, which uses a reconnectingWebSocket to ensure continuous communication to the server, once the connection via websocket is stablished the server will start to continuosly send each object coordinate for the client to draw, each Json object that is being send consists of two bodies, one being the actors bodies and another being the players bodies, each has a different structure and contains different data, such as it's class type, health, state, e.t.c, this informations gets recieved by the useWebSocket for the InvadersGame to use, InvadersGame processes the Json data and then uses useLoadImages to load all images representing the delivered data, for the canvas to deliver the graphical output and draw everything that is contained in the data structures, this react client when given certain input will make a Https Rest request to the server for it to recieve all players scores.

Now, talking about the server this is composed by a static js resource which in simple terms is still the react enviroment, only that is now taking resources from spring to send the configuration to each client, then all data is recieved by InvadersApplication, this is our main class and it has the capabilites to delegate the requests to InvadersController which handles either Https and Wss requests, also manages the InvadersGUI singleton object, the unique and only instance of the game, this instance has a Actor and Player java classes, it's important to denote them because their are the most important data object in this project, now Scache uses the resources folder of the directory, not the Spring instance, if InvadersController recieves a Restful request then it will call UserService to comunicate with the Mongodb cluster, using the user java class as a template it will recieve or create a document with the provided user data, sending the data once again to the Controller, back to the InvadersApplication, and finally to the client for it to display. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

First you need the following java version (command to see your current java version below)

```
java -version
```

![image](https://github.com/Parralol/Lab05ARSW/assets/110953563/87192abf-bebd-4d74-ad1e-e62a94405c43)

to see the maven version we are using we need to enter the following command, also this is the version of Maven this programm uses

```
mvn -version
```

![image](https://github.com/Parralol/Lab05ARSW/assets/110953563/8711cee6-e4ba-47ae-b46c-8984142890bb)


### Installing

First clone this proyect into your own system, then 

```
mvn clean package
```

## Running the tests

To run the automated tests you'll have to type in the console 
```
mvn test
```
this program works with the following tests:

![image](https://github.com/user-attachments/assets/1f7b0d5c-7128-47d5-b9fd-0c678d0b511c)

And a acceptance test

### Break down into end to end tests

1. **Test Player Addition**

    Verify that a player can be added to the game, and that their name and ID are correctly set.

2. **Test Player Name Change**

    Ensure that a player's name can be changed after being added to the game.

3. **Test World Initialization**

    Confirm that the game world initializes correctly, populating the stage with the necessary actors.

4. **Test World Update**

    Ensure that the game world updates correctly, maintaining or changing the number of actors based on game logic.

5. **Test Game Over State**

    Verify that the game transitions to a "game over" state when certain conditions are met, such as player shields reaching zero.

6. **Test Player Retrieval**

    Confirm that players can be retrieved from the game by their ID and that the number of players is tracked accurately.

7. **Test Player Movement**

    Verify that player movement updates their position on the game board correctly based on velocity.

8. **Test Player Collision with Laser**

    Ensure that a player's shields decrease appropriately when colliding with a laser.

9. **Test Player Collision with Monster**

    Confirm that a player's shields decrease by the correct amount when colliding with a monster.

10. **Test Player Firing**

    Verify that firing a bullet adds the bullet actor to the game world.

11. **Test Player Firing Cluster Bomb**

    Ensure that firing a cluster bomb adds multiple bomb actors to the game world and decreases the player's cluster bomb count.

12. **Test Player Lose Condition**

    Confirm that the player is marked as lost if their shields fall below zero.

### Acceptance test


https://github.com/user-attachments/assets/3bc5433a-5113-4ca9-8fad-12b49aabe07e



https://github.com/user-attachments/assets/0b190293-a140-415c-813d-df18021df423



## Generating javadoc

Simply enter the following commands

```
mvn javadoc:javadoc
```

```
mvn javadoc:jar
```

```
mvn javadoc:aggregate
```

```
mvn javadoc:aggregate-jar
```

```
mvn javadoc:test-javadoc 
```

## Deployment

**IN ORDER FOR THIS PROGRAM TO WORK, YOU'LL NEED TO EXECUTE THE PROGRAM ON THE FOLDER YOU WANT TO WORK WITH, WITH THE FILES YOU WANT TO WORK WITH**

if you want to use the programm after using the package command we use

```
mvn spring-boot:run
```

after the server has initialized, you'll have to type in the browser

http://localhost:8080


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Java](https://www.oracle.com/java/technologies/) - Programming Language
* [HTML 5](https://html.spec.whatwg.org/multipage/) - HiperText Markup Lenguaje
* [Spring](https://spring.io/) - Framework
* [React](https://es.react.dev/)- Front-end Libray
  
## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Santiago Parra** - *Initial work* - [Parralol](https://github.com/Parralol)

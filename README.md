# Short introduction

This repo is a POC to test a Spring-Boot application with clustered [Vert.X](http://vertx.io/docs/) within docker enironment. 
Vert.X is an Event-Messaging system and send messages over an eventbus. 
In these POC Vert.X use in background hazelcast to get ome metadata information.

## Start applications

The POC is made of two Spring-Boot applications with an mongo db connection.
Both of the are Nodes in Vert.X sense. 
For start up the apps, go into 
"dockertests/src/main/docker/" directory. Take care that all *.sh files are executable and run ```./run.sh start```  
With ```docker ps``` you will see after 2-3 min. three running healthy container.

## run test
For testing if the aplications can send event messages between them you can run now 
```./run.sh test```
Currently the tests (ConnectionIntegrationTest) are run successfully, because all assertions are disabled and replaced by log messages.  
Have a look into the producerA app by ```docker logs docker_producerA_1``` and see 5 TimeoutExceptions.
The test try to send 10 messages. 5 of them are handled by the producerA host and the other ones will be send to the producerB host. 
But these are failed by timeout.

### Notes
* ```./run.sh clean``` remove and stop all existing docker container and images
* ```./run.sh stop``` will stop the container  
* ```./run.sh help``` for more information
* check health status of app: ```http://localhost:8083/status/health``` or ```http://localhost:8084/status/health```  
* check if can be get access from one host to the other try: 
```docker exec -it docker_producerA_1 /bin/sh``` to get inside of container and then try
```nc -zv producerA [PORT] &> /dev/null; echo $?``` of the container you will connect. You will be find the information about the correct port within /status/health.
If the result of nc is "0" then is the port open. You'll see "1" for closed port.

## Local run

You can also run these POC on local machine and you'll see that all messages be send and no timeout occurs.  
Start at first the mongo docker container by ```./run.sh startEnv``` from the dockertests directory. 
And then go into producer directory and build the jars by: ```mvn clean install```. 
Start the application by: ```mvn spring-boot:run -Dspring.profiles.active=localA``` and ```mvn spring-boot:run -Dspring.profiles.active=localB```
Switch back to the dockertests directory and now you can run the test ```mvn clean verify```, but before move the ignore flag from local test (ConnectionIntegrationTest#sucessfulConnection_ProducerToProducer_onLocal) to the other test method.

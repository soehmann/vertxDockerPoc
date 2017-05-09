FROM openjdk:8-jre-alpine

ARG DOCKER_USER=nobody
ARG DOCKER_GROUP=nobody

WORKDIR /data/producer
RUN mkdir bin logs

RUN chown -R $DOCKER_USER:$DOCKER_GROUP /data/producer

USER ${DOCKER_USER}:${DOCKER_GROUP}

ADD producer.jar bin/app.jar
EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005","-jar","bin/app.jar"]

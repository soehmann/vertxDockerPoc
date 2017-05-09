FROM maven:3-jdk-8-alpine

RUN apk add --no-cache

ARG DOCKER_USER=nobody
ARG DOCKER_GROUP=nobody
ARG USER_HOME_DIR=/home/nobody

RUN mkdir -p $USER_HOME_DIR/.m2
ADD settings.xml $USER_HOME_DIR/.m2/
RUN chown -R $DOCKER_USER:$DOCKER_GROUP $USER_HOME_DIR

RUN mkdir -p /src
RUN mkdir -p /testsrc

USER ${DOCKER_USER}:${DOCKER_GROUP}

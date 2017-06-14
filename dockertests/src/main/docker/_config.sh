#!/usr/bin/env bash

echo "--> loading configuration ..."

export LOCAL_DOCKER_ROOT_DIR=$(pwd)

export LOCAL_PROJECT_ROOT_DIR=${LOCAL_DOCKER_ROOT_DIR}/../../../../


export LOCAL_DOCKER_TESTS_ROOT_DIR=${LOCAL_DOCKER_ROOT_DIR}/../../..
export LOCAL_M2_REPOSITORY=${HOME}/.m2/repository

export DOCKER_USER_HOME=${HOME}
export DOCKER_M2_HOME=${DOCKER_USER_HOME}/.m2
export DOCKER_M2_SETTINGS=${DOCKER_M2_HOME}/settings.xml
export DOCKER_M2_REPOSITORY=${DOCKER_M2_HOME}/repository

export DOCKER_USER_ID=$(id -u)
export DOCKER_USER_GROUP_ID=$(id -g)

export dcApp="docker-compose -f docker-compose-producer.yml"

echo "--> finish loading configuration..."

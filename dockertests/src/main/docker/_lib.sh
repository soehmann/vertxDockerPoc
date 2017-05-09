#!/usr/bin/env bash

set -e

. _config.sh

function runBuildContainer {
    local imageName="$1"
    shift
    local DOCKER_ARGS="$@"


    docker run \
		-e MAVEN_CONFIG=${DOCKER_M2_HOME} \
		--volume ${LOCAL_M2_REPOSITORY}:${HOME}/.m2/repository \
		--volume ${LOCAL_PROJECT_ROOT_DIR}/${imageName}:/src \
		--workdir /src \
		maven \
		${DOCKER_ARGS} # pass all function parameter as args
}

function runTestContainer {
	local MAVEN_COMMAND="$@"
	local containerIds=($(getContainerIds "producer"))

	docker run \
		-e MAVEN_CONFIG=${DOCKER_M2_HOME} \
		--volume ${LOCAL_M2_REPOSITORY}:${HOME}/.m2/repository \
		--volume ${LOCAL_DOCKER_TESTS_ROOT_DIR}:/testsrc \
		--network container:${containerIds[0]} \
		--network container:${containerIds[1]} \
		--workdir /testsrc \
 		maven \
 		${MAVEN_COMMAND}

}

function createMavenImage {
	local imageName="$1"

	echo -n "--> create image ${imageName} ... "

#    cp ${LOCAL_M2_SETTINGS} ${imageName}

	pushd ${imageName} > /dev/null
	docker build  \
		--quiet \
		--tag ${imageName} \
		--build-arg DOCKER_USER=${DOCKER_USER_ID} \
		--build-arg DOCKER_GROUP=${DOCKER_USER_GROUP_ID} \
		--build-arg USER_HOME_DIR=${DOCKER_USER_HOME} \
		.
	popd > /dev/null
	echo "ok"

}

function getContainerIds {
  local containerIds=( $(docker ps -qf ancestor=$1) )

  if [ ${#containerIds[@]} == 0 ]
  	then
  		echo "Error: no running container found for $1"
  		exit 1
  fi

  echo "${containerIds[@]}"
}

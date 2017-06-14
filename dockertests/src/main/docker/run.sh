#!/usr/bin/env bash

set -e # fail on errors
# set -x

. _lib.sh

function buildAppDockerImage {
	local imageName="$1"

	echo -n "--> create app container ... "

	local dockerfile=${LOCAL_PROJECT_ROOT_DIR}/${imageName}/src/main/docker/Dockerfile
	local targetDir=${LOCAL_PROJECT_ROOT_DIR}/${imageName}/target
	ln -f ${dockerfile} ${targetDir}/Dockerfile

	pushd ${targetDir} > /dev/null

	docker build \
		--quiet \
		--build-arg DOCKER_USER=${DOCKER_USER_ID} \
		--build-arg DOCKER_GROUP=${DOCKER_USER_GROUP_ID} \
		--tag ${imageName} . > /dev/null

	popd > /dev/null
	echo "ok"
}

function stopAllContainers {

	set +e
	docker stop $(docker ps -a -q) &> /dev/null
	set -e

}

function buildAppContainer {

    createMavenImage maven

    runBuildContainer consumer mvn -s ${DOCKER_M2_SETTINGS} clean verify -Dmaven.repo.local=${DOCKER_M2_REPOSITORY} -Dmaven.javadoc.skip -Dmaven.source.skip
    runBuildContainer producer mvn -s ${DOCKER_M2_SETTINGS} clean verify -Dmaven.repo.local=${DOCKER_M2_REPOSITORY} -Dmaven.javadoc.skip -Dmaven.source.skip

    buildAppDockerImage consumer
    buildAppDockerImage producer
}

function startProducer {

	stopAllContainers

	createMavenImage maven

    runBuildContainer producer mvn -s ${DOCKER_M2_SETTINGS} clean verify -Dmaven.repo.local=${DOCKER_M2_REPOSITORY} -Dmaven.javadoc.skip -Dmaven.source.skip
    buildAppDockerImage producer

    ${dcApp} up -d --build

    echo "--> container up"

}

function startProducerWithTests {

	stopAllContainers

	createMavenImage maven

    runBuildContainer producer mvn -s ${DOCKER_M2_SETTINGS} clean verify -Dmaven.repo.local=${DOCKER_M2_REPOSITORY} -Dmaven.javadoc.skip -Dmaven.source.skip
    buildAppDockerImage producer

    ${dcApp} up -d --build

    echo "--> container up"

    assertThatAppIsAvailable docker_producerA_1
	assertThatAppIsAvailable docker_producerB_1

	echo "--> start tests on producer"

	test

}

function test {

	local mavenCommand="mvn -s ${DOCKER_M2_SETTINGS} clean verify -Dmaven.repo.local=${DOCKER_M2_REPOSITORY}"

	runTestContainer ${mavenCommand}

}

function assertThatAppIsAvailable {

  	local MAX=180
	local STATUS

	for i in `seq 1 $MAX`
	do
      local RESULT=$(docker ps | grep $1 | grep "(healthy)")
      if [ -z "$RESULT" ]
        then
          sleep 1
        else
          STATUS="$RESULT"
          echo "App is available after ${i}s"
          break
      fi
    done

    if [ -z "$STATUS" ]
      then
      	echo "Fail on startup app with ${MAX}s"
      	exit 1
  	fi

}

function clean {

	stopAllContainers

	set +e
	docker rm $(docker ps -a -q)
	docker rmi $(docker images -a -q)
	set -e

}

function showHelp() {

      cat run_help.txt

}

if [ $# -eq 0 ]; then
    type="help"
else
    type="$1"
    shift # drop first argument
fi

case "${type}" in
    "start")
		startProducer
        ;;
    "startWithTests")
		startProducerWithTests
        ;;
    "test")
        test
        ;;
    "stop")
        stopAllContainers
        ;;
    "clean")
        clean
        ;;
    *)
		showHelp
    	;;
esac

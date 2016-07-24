#!/bin/bash

set -euo pipefail

if [[ ${TRAVIS_PULL_REQUEST} == 'false' && ${TRAVIS_BRANCH} == 'master' ]]
then
  mvn -V -B -e \
    verify -Djdk.version=1.7 --toolchains=./.travis-toolchains.xml \
    sonar:sonar -Dsonar.host.url=${SONARQUBE_URL} -Dsonar.login=${SONARQUBE_TOKEN}
else
  mvn -V -B -e \
    verify -Djdk.version=1.7 --toolchains=./.travis-toolchains.xml
fi

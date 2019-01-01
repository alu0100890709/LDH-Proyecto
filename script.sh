#!/bin/bash

set -xe

TMP=/tmp/.travis_fold_name

# This is meant to be run from top-level dir. of sensorama-ios

travis_fold() {
  local action=$1
  local name=$2
  echo -en "travis_fold:${action}:${name}\r"
}

travis_fold_start() {
  travis_fold start $1
  echo $1
  /bin/echo -n $1 > $TMP
}

travis_fold_end() {
  travis_fold end `cat ${TMP}`
}

#--------------------------------------------------------------------------------


(
  travis_fold_start MAVEN
  mvn -q clean test org.jacoco:jacoco-maven-plugin:prepare-agent package javadoc:javadoc
  travis_fold_end
)

(
  travis_fold_start SONAR
  sonar-scanner
  travis_fold_end
)

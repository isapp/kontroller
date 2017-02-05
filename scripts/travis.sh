#!/bin/bash
set -ev
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  if [ "${TRAVIS_BRANCH}" = "master" ]; then
	./gradlew deployToBintraySnapshot
  elif [ "${TRAVIS_BRANCH}" = "release" ]; then
    ./gradlew deployToBintray
  fi
else
  ./gradlew check
fi
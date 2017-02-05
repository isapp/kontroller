#!/bin/bash
set -ev
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  if [ "${TRAVIS_BRANCH}" = "master" ]; then
	./gradlew deployToBintraySnapshot
  fi
else
  ./gradlew check
fi
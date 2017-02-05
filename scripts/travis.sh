#!/bin/bash
set -ev
./gradlew check
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  if [ "${TRAVIS_BRANCH}" = "master" ]; then
	./gradlew deployToBintraySnapshot
  fi
fi
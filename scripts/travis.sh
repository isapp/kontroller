#!/bin/bash
set -ev
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  if [ "${TRAVIS_BRANCH}" = "master" ]; then
    if [ "${TRAVIS_COMMIT_MESSAGE}" = "Version bump" ]; then
	  echo "Skipping version bump commit"
	else
	  ./gradlew deployToBintraySnapshot
	fi
  elif [ "${TRAVIS_BRANCH}" = "release" ]; then
    ./gradlew deployToBintray
    ./gradlew bumpVersion
  fi
else
  ./gradlew check
fi
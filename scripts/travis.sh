#!/bin/bash
set -ev
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  if [ "${TRAVIS_BRANCH}" = "master" ]; then
    if [ "${TRAVIS_COMMIT_MESSAGE}" = "Bump version" ]; then
	  echo "Skipping version bump commit"
	else
	  ./gradlew deployToBintraySnapshot --stacktrace
	fi
  elif [ "${TRAVIS_BRANCH}" = "release" ]; then
      ./gradlew deployToBintray --stacktrace
      git tag "$(< version)"
      git push https://isapp-ci:${GITHUB_TOKEN}@github.com/isapp/kontroller.git --tags -q

      git config user.email "ci@isapp.com"
      git config user.name "isapp-ci"
      git remote set-branches origin 'master'
      git fetch origin master
      git checkout master
      git pull --rebase
      ./gradlew bumpVersion --stacktrace
      git add version.properties
      git add version
      git commit -m "Bump version"
      git push https://isapp-ci:${GITHUB_TOKEN}@github.com/isapp/kontroller.git master -q
  fi
else
  ./gradlew check --stacktrace
fi

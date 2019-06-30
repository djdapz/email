#!/usr/bin/env bash

GRADLE_HOME="${HOME}/.gradle"
ROOT_FOLDER=$(pwd)
GRADLE_CACHE="${ROOT_FOLDER}/gradle"

if [[ "$GRADLE_CACHE" != "$GRADLE_HOME" ]];
    then
        ln -s ${GRADLE_CACHE} ${GRADLE_HOME}
fi

ls -lsa
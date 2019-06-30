#!/usr/bin/env bash
set -e

./repo/ci/tasks/cache_gradle.sh

export LUKE_DB_URL="jdbc:postgresql://luke-db:5432/luke-test?user=lukeuser&password=lukepwd"
export LUKE_DB_PASSWORD=lukeuser
export LUKE_DB_USERNAME=lukepwd

env
cd repo

./gradlew clean test

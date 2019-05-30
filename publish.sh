#!/bin/sh -ex

export PLUGIN_VERSION=${TRAVIS_TAG}

./gradlew publishPlugins

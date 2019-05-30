#!/bin/sh -ex
echo gradle.publish.key=${GRADLE_KEY} >> "$HOME_DIR/.gradle/gradle.properties"
echo gradle.publish.secret=${GRADLE_SECRET} >> "$HOME_DIR/.gradle/gradle.properties"

export PLUGIN_VERSION=${TRAVIS_TAG}

./gradlew publishPlugins

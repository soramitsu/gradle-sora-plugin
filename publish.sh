#!/bin/sh -ex

export TAG_NAME=${{ env.plugin_tag }}

./gradlew publishPlugins
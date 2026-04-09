#!/bin/sh
# Minimal gradlew wrapper - GitHub Actions uses gradle/wrapper/gradle-wrapper.properties
exec gradle "$@"

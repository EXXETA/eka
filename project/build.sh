#!/usr/bin/env bash

echo "Building jar..."
mvn package

echo "Dockerbuild..."
docker build --build-arg JAR_FILE=target/*jar-with-dependencies.jar --tag eka:0.0.1 .

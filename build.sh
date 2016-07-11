#!/usr/bin/env bash

echo "building the agent.."
cd Agent
gradle build

mkdir $TRAVIS_BUILD_DIR/Agent
cp -r build/libs/* $TRAVIS_BUILD_DIR/Agent

echo "building the client..."
cd ../Server/ui

echo "running npm install..."
npm install

echo "running bower install..."
bower install

echo "running the build.."
gulp build

cd ../
echo "running the gradle build.."
gradle build jacocoTestReport coveralls

mkdir $TRAVIS_BUILD_DIR/Server
cp -r build/libs/* $TRAVIS_BUILD_DIR/Server


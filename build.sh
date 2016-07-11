#!/usr/bin/env bash

echo "building the agent.."
cd Agent
gradle build

echo "list current dir:"
ls -al
mkdir $TRAVIS_BUILD_DIR/dist/Agent
cp -r build/libs/* $TRAVIS_BUILD_DIR/dist/Agent

echo "list current dir:"
ls $TRAVIS_BUILD_DIR/dist/Agent  -al

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

echo "list current dir:"
ls -al

mkdir $TRAVIS_BUILD_DIR/dist/Server
cp -r build/libs/* $TRAVIS_BUILD_DIR/dist/Server

echo "list current dir:"
ls $TRAVIS_BUILD_DIR/dist/Server  -al


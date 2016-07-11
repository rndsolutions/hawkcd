#!/usr/bin/env bash

echo "building the agent.."
cd Agent
gradle build

echo "$TRAVIS_BUILD_DIR" : $TRAVIS_BUILD_DIR

echo "list current dir:"
ls -al

cd ../
mkdir dist
cd dist
mkdir Agent
cd ../Agent/
cp -r build/libs/* ../dist/Agent

echo "building the client..."
cd ../Server/ui

echo "running npm install..."
npm install

echo "running bower install..."
bower install

echo "running the build.."
gulp build

cd ../
echo "running the gradle build.."g
gradle build jacocoTestReport coveralls

echo "list current dir:"
ls -al


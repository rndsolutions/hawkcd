#!/usr/bin/env bash

echo "building the agent.."
cd Agent
gradle build

echo "$TRAVIS_BUILD_DIR" : $TRAVIS_BUILD_DIR

echo "list current dir:"
ls -al

echo "creating dist folder.."
cd ../
mkdir dist

echo "list current dir:"
ls -al

cd dist
mkdir Agent
mkdir Server

echo "list current dir:"
ls -al

cd ../Agent/
cp -r build/libs/* ../dist/Agent

echo "list current dir:"
ls -al

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

echo "current directory: " pwd

echo "copy files server build outputs to the dist folder"
cp -r build/libs/* ../dist/Server

echo "list current dir:"
ls -al


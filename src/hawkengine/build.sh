#!/usr/bin/env bash

echo "building the client..."
cd ui

echo "running npm install..."
npm install

echo "running bower install..."
bower install

echo "running the build.."
gulp build

cd ../
echo "running the gradle build.."
gradle build jacocoTestReport coveralls


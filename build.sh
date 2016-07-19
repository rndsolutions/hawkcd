#!/usr/bin/env bash

#fails the build if the exit code is differen than 0
function failBuild  {
    if [ $? -eq 0 ]
        then
            echo "Successfully created file"
         else
            echo "Error building agent" >&2
           exit;
    fi
 }

echo "building the agent.."
cd Agent

gradle build
failBuild

#echo "list current dir:"
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
failBuild

cd ../
echo "running the gradle build.."g
gradle build jacocoTestReport coveralls
failBuild

echo "copy files server build outputs to the dist folder"
cp -r build/libs/* ../dist/Server

echo "list current dir:"
ls -al



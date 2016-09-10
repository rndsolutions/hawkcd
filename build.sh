#!/usr/bin/env bash

#fails the build if the exit code is differen than 0
function failBuild  {
    if [ $? -eq 0 ]
        then
            echo "Successfully completed"
         else
            echo "Error building" >&2
           exit 1;
    fi
 }


echo "building the agent.."
cd Agent

gradle clean
gradle build
failBuild

#echo "list current dir:"
ls -al

echo "creating dist folder.."
cd ../

if [ -d "dist" ]; then
    rm -rf dist
    failBuild
fi

mkdir dist

echo "list current dir:"
ls -al

cd dist
mkdir Agent
mkdir Server

echo "list current dir:"
ls -al

cd ../Agent/
cp build/libs/Agent-all.jar ../dist/Agent

echo "list current dir:"
ls -al

echo "cleaning up resources folder..."
cd ../Server/src/main/resources/



echo "building the client..."
cd  ../../../ui

echo "running npm install..."
npm install

echo "running bower install..."
bower install

echo "running the build.."
gulp build
failBuild

cd ../
echo "running the gradle build.."
gradle clean
gradle build
failBuild
gradle jacocoTestReport
failBuild
gradle coveralls
failBuild


echo "copy files server build outputs to the dist folder"
cp  build/libs/Server-all.jar ../dist/Server

cp ../Server/hawkcd.sh ../dist/Server/

cp ../Agent/agent.sh ../dist/Agent/

cd ../dist/Server
wget http://www.hawkcd.io/downloads/dependences/redis.tar.gz
tar zxvf redis.tar.gz
rm redis.tar.gz

#create distribution packages
echo "Create distribution packages"
cd ../
tar -cvzf hawkcd.tar.gz Server
tar -cvzf hawkcd-agent.tar.gz Agent

echo -e "Build finished successfully"



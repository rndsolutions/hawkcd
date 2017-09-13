#!/usr/bin/env bash

#project dir
prj_dir=pwd
build_script="build1.sh"

agent_base_name="agent"
server_base_name="hawkcd"
version="0.0.5"

#save to a file the latest commit
git show > rev.txt

arg1=$1

if [[ -z $2 ]]; then
  revision="alpha.xx"
else
  revision="alpha."$2
fi

agent_artifact="$agent_base_name-$version-$revision.jar"
server_artifact="$server_base_name-$version-$revision.jar"


echo "Arguments:  $arg1 , $revision "
echo "Agent Artifact: $agent_artifact"
echo "Server Artifact: $server_artifact"

#utill
function clean {
  #clean up dist folder
  if [ -d "dist" ]; then
      rm -rf dist
      exit_on_fail
  fi
}
function _init {
  #create install image folder
  if [ ! -d "dist" ]; then
      mkdir dist
      cd dist
      mkdir Agent
      mkdir Server
      cd ../
  fi
}
function exit_on_fail  {
    if [ $? -eq 0 ]
        then
            echo "Successfully completed"
         else
            echo "Error building" >&2
           exit 1;
    fi
}

if [ ! -f "$build_script" ]; then
  echo "error: build script:  $build_script does not exist in the current dir"
  exit 1
fi

# initilize
_init

function compile_agent {
  echo "building the agent.."
  rm -rf $($prj_dir)/dist/Agent/*

  #cd Agent
  gradle -b $($prj_dir)/Agent/build.gradle clean
  gradle -b $($prj_dir)/Agent/build.gradle -Pbase_Name="$agent_base_name" -PbuildVersion="$version" -Prevision="$revision" build -x test

  cp $($prj_dir)/rev.txt dist/Agent
  cp $($prj_dir)/Agent/agent.sh dist/Agent
  sed -i "s/<replace>/$agent_base_name-$version-$revision/g" $($prj_dir)/dist/Agent/agent.sh

  cp "$($prj_dir)/Agent/build/libs/$agent_artifact" dist/Agent
  exit_on_fail
}
function run_agent_unit_test {
  echo "building the agent.."
  gradle -b $($prj_dir)/Agent/build.gradle test
  exit_on_fail
}
function package_agent_nix {
  echo "packaging agent.."
  rm -rf $($prj_dir)/dist/hawkcd-agent.tar.gz

  cd dist
  tar -cvzf hawkcd-agent.tar.gz Agent
  cd ../
  exit_on_fail
}
function package_agent_win {
  echo "to be implemented"
}

function build_ui {

  if [ -d "$($prj_dir)/Server/src/main/resources/dist" ]; then
      rm -rf $($prj_dir)/Server/src/main/resources/dist
      exit_on_fail
  fi
  #npm --prefix ./some_project
  #npm --prefix $($prj_dir)/Server/ui install
  exit_on_fail
  cd $($prj_dir)/Server/ui
  npm install
  bower install
  exit_on_fail
  gulp build
  exit_on_fail
  cd ../../  
}

function compile_server {
  build_ui

  echo "building the server.."
  rm -rf $($prj_dir)/dist/Server/*

  #cd Agent
  gradle -b $($prj_dir)/Server/build.gradle clean
  gradle -b $($prj_dir)/Server/build.gradle -Pbase_Name="$server_base_name" -PbuildVersion="$version" -Prevision="$revision" build -x test

  echo "$prj_dir"
  cp $($prj_dir)/rev.txt dist/Server
  cp "$($prj_dir)/Server/build/libs/$server_artifact" dist/Server
  cp $($prj_dir)/Server/hawkcd.sh dist/Server

  sed -i "s/<replace>/$server_base_name-$version-$revision/g" $($prj_dir)/dist/Server/hawkcd.sh

  exit_on_fail
}

function run_server_unit_test {
  #statements
  echo "running server tests.."
  gradle -b $($prj_dir)/Server/build.gradle test
  exit_on_fail
}

function package_server_win {
  echo pwd
}

function package_server_nix {
  #statements
  cd $($prj_dir)/dist/Server
  wget  http://www.hawkcd.io/downloads/dependences/redis.tar.gz
  tar zxvf redis.tar.gz
  rm redis.tar.gz
  cd ..
  #package
  tar -cvzf hawkcd.tar.gz Server
  cd ../
  exit_on_fail
}

function build_all {
  compile_agent
  run_agent_unit_test
  package_agent_nix

  compile_server
  #run_server_unit_test
  package_server_nix
}

function list {
  echo "usage: ./build.sh <command> "
  echo
  echo "  run_agent_unit_test"
  echo "  package_agent_nix"
  echo "  package_agent_win"
  echo "  compile_server"
  echo "  build_ui"
  echo "  run_server_unit_test"
  echo "  package_server_nix"
  echo "  package_server_win"
  echo "  build_all"
  echo

}

case "$arg1" in
"compile_agent")
      compile_agent
    ;;
"run_agent_unit_test")
      run_agent_unit_test
    ;;
"package_agent_nix")
      package_agent_nix
    ;;
"package_agent_win")
      package_agent_win
    ;;
"compile_server")
      compile_server
    ;;
"build_ui")
      build_ui
    ;;
"run_server_unit_test")
      run_server_unit_test
    ;;
"package_server_nix")
      package_server_nix
    ;;
"package_server_win")
      package_server_nix
    ;;
"build_all")
      "build_all"
    ;;
*)
    "list"
    ;;
esac

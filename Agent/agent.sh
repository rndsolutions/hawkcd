#!/bin/bash

version=<replace>

RED='\033[0;31m'
GREEN='\033[0;32m'
RESET="$(tput sgr0)"

function start {
  #check for java
  #echo "hi"
  check_java
  is_prereq_met=$?
  if [[ "$is_prereq_met" -eq 0 ]]; then
    #statements
    start_agent
  fi
}

function stop {
  #statements\
    if [[ -z $(pgrep -x -f  "java -jar $version.jar") ]]; then
      #statements
      echo "Agent is not running"
    else
      pgrep -x -f  "java -jar $version.jar" | kill $(tail)
      echo "Agent stopped"
    fi
}


function check_java {
  if type -p java; then
  #statements
    _java=java
    JAVA_EXISTS=true;
  elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
      echo -e "${GREEN}Found java executable in JAVA_HOME ${RESET}"
      _java="$JAVA_HOME/bin/java"
      JAVA_EXISTS=true;
  else
      echo -e "${RED} java installation was not found ${RESET}"
      return 1;
  fi
  if [[ "$_java" ]]; then
      JAVA_VERSION=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
  fi

  if [ -n "$JAVA_EXISTS" ]; then
      echo -e "${GREEN}found java executable in PATH  $(type -p java) ${RESET}"
  else
      echo "Java not found"
      return 1;
  fi

  if [[ "$JAVA_VERSION" > "1.8" ]]; then
      echo -e "${GREEN}Java version $JAVA_VERSION is found ${RESET}"
  else
      echo -e "${RED} java 1.8 or above is required ${RESET}"
      return 1;
  fi
}


function start_agent {
  #statements
  java -jar "$version".jar &
}


function list {
  echo "---Available commands---"
  echo "start - starts database and application server"
  echo "stop  - stops database and application server"
  echo "check - checks if the enviroment has all required software"
  echo "--------End-------------"
}

#read input args
tmp1=$1 ;tmp2=$2; tm3=$3;

#set default values
func=${tmp1:-'start'}
db_port=${tmp2:='6379' }
server_port=${tmp3:-'8080' }

case "$func" in
"start")
      is_running=$(pgrep -x -f  "java -jar Agent-all.jar")
      echo $is_running
      if [[ -z $is_running ]]; then
        "start"
      else
         echo -e "${RED}The Agent is already running ${RESET}"
         "list"
      fi
    ;;
"stop")
   "stop"
    ;;
 "check")
    "check"
    ;;
*)
    "list"
    ;;
esac

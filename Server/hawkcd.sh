 # Copyright (C) 2016 R&D Solutions Ltd.
 #
 # Licensed under the Apache License, Version 2.0 (the "License");
 # you may not use this file except in compliance with the License.
 # You may obtain a copy of the License at
 #
 # http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.

#!/bin/bash

version=<replace>

RED='\033[0;31m'
GREEN='\033[0;32m'
RESET="$(tput sgr0)"

function start {
  #check for java
  #echo "hi"
  check
  is_prereq_met=$?
  if [[ "$is_prereq_met" -eq 0 ]]; then
    #statements
    start_db
    start_server
  fi
}
function stop {
  #statements\
    if [[ -z $(pgrep -x -f  "java -jar $version.jar") ]]; then
      #statements
      echo "hawkcd is not running"
    else
      pgrep -x -f  "java -jar $version.jar" | kill $(tail)
      echo "Application server stopped"
    fi

    if [[ -z $(pgrep redis-server) ]]; then
      echo "redis is not running"
    else
      pgrep redis-server | kill $(tail)
    fi
}
function check {
  #check java version
  check_java
  is_java_available=$?

  check_open_port $db_port "redis"
  is_redis_port_open=$?

  check_open_port $server_port "hawkcd"
  is_hawkcd_default_port_open=$?

  if [[ $is_redis_port_open -eq 1 ]] || [[ $is_hawkcd_default_port_open -eq 1 ]]; then
    #statements
    return 1
  else
    return 0
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

  if [[ "$JAVA_VERSION" > '1.8' ]]; then
      echo -e "${GREEN}Java version $JAVA_VERSION is found ${RESET}"
  else
      echo -e "${RED} java 1.8 or above is required ${RESET}"
      return 1;
  fi
}
function check_open_port {
  #statements
  _port="$1"
  server="$2"
  nc -z localhost $_port
  _is_port_in_use=$?
  if [ "$_is_port_in_use" -eq "0" ]; then
    #statements
    echo -e "${RED}Default $server port $_port is in use${RESET}"
    return 1;
  else
    echo -e "${GREEN}Default $server port $_port is free${RESET}"
    return 0;
  fi
}
function start_server {
  #statements
  java -jar $version.jar &
}
function start_db {
  data/Linux/redis/redis-server &
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
      is_running=$(pgrep -x -f  "java -jar $version.jar")
      echo $is_running
      if [[ -z $is_running ]]; then
        "start"
      else
         echo -e "${RED}The server is already running ${RESET}"
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

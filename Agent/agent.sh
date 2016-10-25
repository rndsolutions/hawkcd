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

function upgrade {
  #./agent.sh upgrade /path
  INSTALL_DIR=$tmp2
  #check if the directory
  echo $INSTALL_DIR
  if [[ -d "$INSTALL_DIR" ]]; then
    #check if agent.sh file exists
    echo "checking Install dir"
    if [[ -f "$INSTALL_DIR/agent.sh" ]]; then
       ls -al "$INSTALL_DIR"
       #extracts the jar version to upgrade
       result=$(find "$INSTALL_DIR" -name 'agent-*' -type f | grep -P -o "agent-\d.\d.\d*-(alpha|beta|rc).\d*")
       #echo "$result"
       _name=$(grep -P -o "agent" <<< $result)
       _version=$(grep -P -o "(\d{1,2}.\d{1,4}.\d{1,4})" <<< $result)
       _qualifier=$(grep -P -o "(alpha|beta|rc)" <<< $result)
       _revision=$(grep -P -o "(\d{1,4}$)" <<< $result)
       echo "hello from upgrade: $_version"
       #revision
       can_upgrade $_name $_version $_qualifier $_revision
    fi
  else
    echo -e "${RED}Can't find Install Dir: Please provide path to the Agent Instllation directory${RESET}"
  fi
}

function can_upgrade {
  _name=$1
  _version=$2
  _qualifier=$3
  _rev=$4

  full_version_to_upgrade="$_name-$_version.$_qualifier.$_rev"
  echo "preparing to upgrade... $full_version_to_upgrade to $version "
  _current=$(grep -P -o "(\d{1,2}.\d{1,4}.\d{1,4})" <<< $version)
  _ver_to_upgrade=$2

  echo "_current: $_current _ver_to_upgrade: $_ver_to_upgrade"
  #check version numbers
  if [[ "$current" < "$ver_to_upgrade" || "$_current" = "$ver_to_upgrade" ]]; then
    _current=$(grep -P -o "(alpha|beta|rc)" <<< $version)
    _c_qualifier=$_current
    #echo "_current: $_current _qualifier: $_qualifier"
    #check qualifiers
    if [[ $_current < $_qualifier || $_current = $_qualifier ]]; then
        _current=$(grep -P -o "(\d{1,4}$)" <<< $version);
        #echo "_current: $_current _revision: $_rev"
        #check revisions
        if [[ $_current < $_rev || $_current = $_rev ]]; then
          #statements
          echo " Can not upgrade.. $full_version_to_upgrade to $version"
          return 1
        else if [[ $_c_qualifier > $_qualifier ]]; then
          #statements
          echo "Upgradeing.. $full_version_to_upgrade to $version"
        fi
        echo " Can not upgrade.. $full_version_to_upgrade to $version"
          return 0;
        fi #end revision
        echo " Can not upgrade.. $full_version_to_upgrade to $version"
        return 1
    else
      echo "Upgradeing... $full_version_to_upgrade to  $version"
      return 0
    fi #end qualifier
  else
    echo "Upgradeing... $full_version_to_upgrade to  $version"
  fi #end version

  return 0
}

function version {
  #statements
  echo "$version"
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
"upgrade")
        echo "calling upgrade"
       "upgrade"
       ;;
"version")
       "version"
       ;;
*)
    "list"
    ;;
esac

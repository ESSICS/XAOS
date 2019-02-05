#!/bin/bash
#

LIBS_PATH=./libs
MODS_PATH=./mods
APPLICATION_VERSION="0.4.0-SNAPSHOT"
MAIN_MODULE=xaos.demos.simple.application
MAIN_JAR=${MAIN_MODULE}-${APPLICATION_VERSION}.jar
	MODULES=`jdeps --module-path ${MODS_PATH} -recursive -summary -filter:module ${MODS_PATH}/${MAIN_JAR} | sed -n -e 's/^.*-> //p' | sort | uniq | tr '\n' ',' | sed '$s/.$//'`

java \
  -cp ${LIBS_PATH} \
  -p ${MODS_PATH} \
  --add-modules ${MODULES} \
  --module ${MAIN_MODULE}/se.europeanspallationsource.xaos.demos.simple.SimpleApplication
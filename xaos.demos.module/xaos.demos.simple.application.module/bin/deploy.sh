#!/bin/bash
#

#
# TODO:CR not working yet
#

cd target

JAVAFX_MODS_PATH=/Library/Java/JavaVirtualMachines/javafx-jmods-11.0.2
PACKAGER=/Library/Java/JavaVirtualMachines/jdk.packager/jpackager
JARS_PATH=./installer-input
OUTPUT=./installer
APPLICATION_NAME="Simple Application"
APPLICATION_VERSION="0.4.1"
MAIN_MODULE=xaos.demos.simple.application
MAIN_JAR=${MAIN_MODULE}-${APPLICATION_VERSION}.jar
MODULES=`jdeps --module-path ${JARS_PATH} -recursive -summary -filter:module ${MAIN_JAR} | sed -n -e 's/^.*-> //p' | sort | uniq | tr '\n' ',' | sed '$s/.$//'`

#${PACKAGER} create-installer pkg \
${PACKAGER} create-image \
  --verbose \
  --echo-mode \
  --module-path ${JAVAFX_MODS_PATH},${JARS_PATH} \
  --add-modules ${MODULES},${MAIN_MODULE} \
  --input ${JARS_PATH} \
  --output ${OUTPUT} \
  --name "${APPLICATION_NAME}" \
  --main-jar ${MAIN_JAR} \
  --module ${MAIN_MODULE} \
  --version ${APPLICATION_VERSION} \
  --class se.europeanspallationsource.xaos.demos.simple.SimpleApplication


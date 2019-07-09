#!/bin/bash
#
# ******************************************************************************
#     Completes the preparation of the deployable product
# ******************************************************************************


BIN_PATH=./bin
TARGET_PATH=./target
PRODUCT_PATH="${TARGET_PATH}/product"
MODS_PATH="${PRODUCT_PATH}/mods"
LIBS_PATH="${PRODUCT_PATH}/libs"
MAIN_JAR="${TARGET_PATH}/$1"
DOC_PATH=./doc


# Remove all test dependencies...
rm -v "${MODS_PATH}/assertj-core-3.11.1.jar"
rm -v "${MODS_PATH}/hamcrest-core-1.3.jar"
rm -v "${MODS_PATH}/junit-4.12.jar"


# Move non-module JARs into the class path...
if [ ! -d "${LIBS_PATH}" ]; then
  mkdir -pv "${LIBS_PATH}"
fi

#mv -v "${MODS_PATH}/xxx.jat" "${LIBS_PATH}/"


# Copy product main JAR in module path...
cp -v "${MAIN_JAR}" "${MODS_PATH}/"


# Copy run script in the product folder.
cp -v "${BIN_PATH}/run.sh" "${PRODUCT_PATH}/"


# Super call to main fixup.sh
../../bin/fixup.sh $1 $2


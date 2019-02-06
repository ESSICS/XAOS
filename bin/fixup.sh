#!/bin/bash
#
# ******************************************************************************
#     Completes the preparation of the deployable product
# ******************************************************************************


TARGET_PATH=./target
PRODUCT_PATH="${TARGET_PATH}/product"
MODS_PATH="${PRODUCT_PATH}/mods"
MAIN_JAR="${TARGET_PATH}/$1"
DOC_PATH=./doc
DOT_BIN="$2"


# Creating modules dependency overview documentation image...
if [ -f "${MAIN_JAR}" ]; then
  rm -fv "${DOC_PATH}/summary.dot"
  rm -fv "${DOC_PATH}/module-dependencies.dot"
  rm -fv "${DOC_PATH}/module-dependencies.png"
  jdeps --dot-output "${DOC_PATH}" --module-path "${MODS_PATH}" -recursive -summary -filter:module "${MAIN_JAR}"
  mv -v "${DOC_PATH}/summary.dot" "${DOC_PATH}/module-dependencies.dot"
  "${DOT_BIN}" -T png -o "${DOC_PATH}/module-dependencies.png" "${DOC_PATH}/module-dependencies.dot"
fi


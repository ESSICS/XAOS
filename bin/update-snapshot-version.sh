#!/bin/bash
set -e

# Check parameters
if [ $# != 1 ]
then
  echo You must provide the product version \(e.g. \"./update-snapshot-version.sh 3.3.0\"\).
  exit -1
fi

VERSION=$1

cd ..
mvn versions:set -DnewVersion=${VERSION}-SNAPSHOT -DprocessAllModules
mvn versions:commit -DprocessAllModules
cd bin

# TODO:CR sed README.md to update version number in the maven examples

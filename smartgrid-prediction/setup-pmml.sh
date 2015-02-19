#!/bin/sh

# Get the location of this script
pushd `dirname $0` > /dev/null
SCRIPTPATH=`pwd`
popd > /dev/null

# Create a temp dir to checkout the pmml sources
MYTEMPDIR=`mktemp -d 2>/dev/null || mktemp -d -t 'mytmpdir'`
cd $MYTEMPDIR

git clone git@github.com:spring-projects/spring-xd-modules.git
cd spring-xd-modules/analytics-ml-pmml

# Copy the prediction.pmml file so that it is embedded inside the module itself
cp $SCRIPTPATH/prediction/prediction.pmml ./src/main/resources

# Build the module
./gradlew clean test bootRepackage

# Install it in Spring XD
curl -v --data-binary \
	@./build/libs/spring-xd-analytics-ml-pmml-1.2.0.BUILD-SNAPSHOT.jar \
	-H "Content-Type: application/octet-stream" \
	http://localhost:9393/modules/processor/analytic-pmml


#!/bin/sh

mytmpdir=`mktemp -d 2>/dev/null || mktemp -d -t 'mytmpdir'`
cd $mytmpdir
git clone /Users/ebottard/Documents/projects/spring-xd-modules/.git
cd spring-xd-modules/analytics-ml-pmml
./gradlew clean test bootRepackage

curl -v --data-binary \
	@./build/libs/spring-xd-analytics-ml-pmml-1.2.0.BUILD-SNAPSHOT.jar \
	-H "Content-Type: application/octet-stream" \
	http://localhost:9393/modules/processor/analytic-pmml


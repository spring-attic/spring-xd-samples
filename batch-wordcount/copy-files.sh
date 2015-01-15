#!/bin/sh
mkdir -p $XD_HOME/custom-modules/job/wordcount/config
mkdir -p $XD_HOME/custom-modules/job/wordcount/lib
cp target/batch-wordcount-1.0.0.BUILD-SNAPSHOT-bin/modules/job/* $XD_HOME/custom-modules/job/wordcount/config
cp target/batch-wordcount-1.0.0.BUILD-SNAPSHOT-bin/lib/* $XD_HOME/lib
cp target/batch-wordcount-1.0.0.BUILD-SNAPSHOT-bin/nietzsche-chapter-1.txt /tmp

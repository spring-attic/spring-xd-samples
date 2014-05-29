#!/bin/sh
mkdir -p $XD_HOME/modules/job/hashtagcount/config
mkdir -p $XD_HOME/modules/job/hashtagcount/lib
cp target/batch-hashtag-count-1.0.0.BUILD-SNAPSHOT-bin/modules/job/* $XD_HOME/modules/job/hashtagcount/config
cp target/batch-hashtag-count-1.0.0.BUILD-SNAPSHOT-bin/lib/* $XD_HOME/modules/job/hashtagcount/lib

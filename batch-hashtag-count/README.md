Spring XD Batch Hashtag Count Sample
=================================

This sample will take an input file with Twitter JSON data and counts the occurrences of hashtags.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))
* Hadoop ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Hadoop-Installation))

## Building

Build the sample simply by executing:

	$ mvn clean assembly:assembly

As a result, you will see the following files and directories created under `target/batch-hashtag-count-1.0.0.BUILD-SNAPSHOT-bin/`:

```
|-- batch-hashtag-count-1.0.0.BUILD-SNAPSHOT-bin
|   |-- lib
|   |   `-- hadoop-examples-1.1.2.jar
|   |-- modules
|   |   `-- job
|   |       `-- hashtagcount.xml
```

the modules/job directory defines the location of the file to import, HDFS directories to use as well as the name node location.  You can verify the settings inside hashtagcount.xml.  All relevant properties are defined in the util:property element:

	<util:properties id="myProperties" >
		<prop key="tweets.input.path">/xd/tweets/</prop>
		<prop key="tweets.output.path">/hashtagcount/out/</prop>
		<prop key="hd.fs">hdfs://localhost:8020</prop>
	</util:properties>

Please verify particularly the following property:

* hd.fs - The [Hadoop NameNode](http://wiki.apache.org/hadoop/NameNode) to use. The setting should be fine, but the port may be different between Hadoop versions (e.g. port 9000 is common also)

## Running the Sample

In the batch-directory

	$ cp target/batch-hashtag-count-1.0.0.BUILD-SNAPSHOT-bin/modules/job/* $XD_HOME/modules/job
	$ cp target/batch-hashtag-count-1.0.0.BUILD-SNAPSHOT-bin/lib/* $XD_HOME/lib

Now your Sample is ready to be executed. Start your *Spring XD* admin server (If it was already running, you must restart it):

	xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

## Collect Twitter Data

	$ stream create --name tweets --definition "twitterstream | hdfs --rollover=10000000" --deploy

## Create the Batch Job

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create --name hashtagCountJob --definition "hashtagcount" --deploy

Launch the job using:

	xd:>job launch hashtagCountJob

You should see a message:

	Successfully created and deployed job 'hashtagCountJob'

## Verify the result

First specify the Hadoop NameNode for the Spring XD Shell:

	xd:>hadoop config -fs --namenode hdfs://localhost:8020
	
We will now take a look at the root of the *HDFS* filesystem:
	
	xd:>hadoop fs ls /

You should see output like the following:

	Found 4 items
	drwxr-xr-x   - hillert supergroup          0 2013-08-12 11:01 /hashtagcount
	drwxr-xr-x   - hillert supergroup          0 2013-08-09 11:31 /user
	drwxr-xr-x   - hillert supergroup          0 2013-08-08 10:53 /xd

As we declared the property `tweets.output.path` in **hashtagcount.xml** to be `/hashtagcount/out/`, let's have a look at the respective directory:

	xd:>hadoop fs ls /hashtagcount/out
	Found 2 items
	-rw-r--r--   3 hillert supergroup          0 2013-08-10 00:07 /hashtagcount/out/_SUCCESS
	-rw-r--r--   3 hillert supergroup      31752 2013-08-10 00:07 /hashtagcount/out/part-r-00000

Finally, executing:

	xd:>hadoop fs -cat /hashtagcount/out/part-r-00000

should yield a long list of hashtags, indicating the number of occurrences within the provided input snapshot of Twitter data.


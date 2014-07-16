Spring XD Batch Hashtag Count Sample
=================================

This sample will take an input file with Twitter JSON data and counts the occurrences of hashtags.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))
* Hadoop ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Hadoop-Installation))

Furthermore you must have your Twitter API credentials ready:

* Consumer Key
* Consumer Secret
* Access Token
* Access Token Secret

## Building

Build the sample simply by executing:

	$ mvn clean assembly:assembly

As a result, you will see the following files and directories created under `target/batch-hashtag-count-1.0.0.BUILD-SNAPSHOT-bin/`:

```
|-- lib
|   `-- batch-hashtag-count-1.0.0.BUILD-SNAPSHOT.jar
`-- modules
    `-- job
        `-- hashtagcount.xml
```

the modules/job directory defines the location of the file to import, HDFS directories to use as well as the name node location.  You can verify the settings inside `hashtagcount.xml`.  All relevant properties are defined in the `util:property` element:

	<util:properties id="myProperties" >
		<prop key="tweets.input.path">/xd/tweets/</prop>
		<prop key="tweets.output.path">/xd/hashtagcount/out/</prop>
		<prop key="hd.fs">hdfs://localhost:8020</prop>
	</util:properties>

Please verify particularly the following property:

* hd.fs - The [Hadoop NameNode](http://wiki.apache.org/hadoop/NameNode) to use. The setting should be fine, but the port may be different between Hadoop versions (e.g. port 9000 is common also)

## Running the Sample

In the batch-directory execute

	$ ./copy-files.sh

Which will copy the job definition and jar file into $XD_HOME/modules/job/hashtagcount

Now your sample is ready to be executed.  The simplest way to run Spring XD is using the singlenode server.

	xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

## Collect Twitter Data

In order to setup the Twitter stream, you must either provide your Twitter API credentials via the shell:

```
xd:> stream create --name tweets --definition "twitterstream \
--consumerKey='your_credentials' \
--consumerSecret='your_credentials' \
--accessToken='your_credentials' \
--accessTokenSecret='your_credentials' | hdfs --rollover=2M" --deploy
```

or alternatively you can provide the credentials via:

`config/modules/source/twitterstream/twitterstream.properties`

```
consumerKey=<your consumer key>
consumerSecret=<your consumer secret>
accessToken=<your access token>
accessTokenSecret=<your token secret>
```

That way you don't have to provide your credentials every time you create a stream:

	xd:> stream create --name tweets --definition "twitterstream | hdfs --rollover=2M" --deploy

## Create the Batch Job

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create --name hashtagCountJob --definition "hashtagcount" --deploy

Launch the job using:

	xd:>job launch hashtagCountJob

You should see a message:

	Successfully created and deployed job 'hashtagCountJob'

## Verify the result

First specify the Hadoop NameNode for the Spring XD Shell:

	xd:>hadoop config fs --namenode hdfs://localhost:8020
	
We will now take a look at the root of the *HDFS* filesystem:
	
	xd:>hadoop fs ls /xd

You should see output like the following:

	Found 2 items
	drwxr-xr-x   - hillert supergroup          0 2013-08-12 11:01 /xd/hashtagcount
	drwxrwxrwx   - hillert supergroup          0 2013-08-12 11:00 /xd/tweets

As we declared the property `tweets.output.path` in **hashtagcount.xml** to be `/xd/hashtagcount/output/`, let's have a look at that directory:

	xd:>hadoop fs ls /xd/hashtagcount/output
	Found 2 items
	-rw-r--r--   3 hillert supergroup          0 2013-08-10 00:07 /xd/hashtagcount/output/_SUCCESS
	-rw-r--r--   3 hillert supergroup      31752 2013-08-10 00:07 /xd/hashtagcount/output/part-r-00000

Finally, executing:

	xd:>hadoop fs cat /xd/hashtagcount/output/part-r-00000

should yield a long list of hashtags, indicating the number of occurrences within the provided input of Twitter data.


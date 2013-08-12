Spring XD Batch Word-count Sample
=================================

This is the [*Spring Batch* word-count sample](https://github.com/SpringSource/spring-data-book/tree/master/hadoop/batch-wordcount) for Hadoop adapted for *Spring XD*. This sample will take an input file and counts the occurrences of each word within that document.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))
* Hadoop ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Hadoop-Installation))

## Building

Build the sample simply by executing:

	$ mvn clean assembly:assembly

As a result, you will see 2 directories that were created under `target/batch-wordcount-1.0.0.BUILD-SNAPSHOT-bin/`:

* copy-contents-to-modules-job
* copy-contents-to-lib

Inside the *copy-contents-to-modules-job* directory, verify the settings in **wordcount-context.xml**. All relevant properties are defined at:

	<util:properties id="myProperties" >
		<prop key="wordcount.input.path">/count/in/</prop>
		<prop key="wordcount.output.path">/count/out/</prop>
		<prop key="local.data.file">data/nietzsche-chapter-1.txt</prop>
		<prop key="hd.fs">hdfs://localhost:8020</prop>
	</util:properties>

Please verify particularly the following 2 properties:

* local.data.file - Points to the location of the file whose words ought to be counted
* hd.fs - The [Hadoop NameNode](http://wiki.apache.org/hadoop/NameNode) to use. The setting should be fine, but the port may be different between Hadoop versions (e.g. port 9000 is common also)

As an example, we will use the data file `nietzsche-chapter-1.txt` that you will find in directory `target/batch-wordcount-1.0.0.BUILD-SNAPSHOT-bin/`. Please change the property `local.data.file`, so that its value points to the **absolute** location of `nietzsche-chapter-1.txt`.

## Running the Sample

1. Copy the **wordcount-context.xml** file to your *Spring XD* home directory under `modules/job`.
2. From directory `copy-contents-to-lib` copy the dependency `hadoop-examples-1.1.2.jar` to the `lib/` directory inside your *Spring XD* home directory.

Now your Sample is ready to be executed. Start your *Spring XD* admin server (If it was already running, you must restart it):

	xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create --name wordCountJob --definition "wordcount"

You should see a message:

	Successfully created and deployed job 'wordCountJob'

## Verify the result

First specify the Hadoop NameNode for the Spring XD Shell:

	xd:>hadoop config fs --namenode hdfs://localhost:8020
	
We will now take a look at the root of the *HDFS* filesystem:
	
	xd:>hadoop fs ls /

You should see output like the following:

	Found 4 items
	drwxr-xr-x   - hillert supergroup          0 2013-08-06 22:35 /Users
	drwxr-xr-x   - hillert supergroup          0 2013-08-12 11:01 /count
	drwxr-xr-x   - hillert supergroup          0 2013-08-09 11:31 /user
	drwxr-xr-x   - hillert supergroup          0 2013-08-08 10:53 /xd

As we declared the property `wordcount.output.path` in **wordcount-context.xml** to be `/count/out/`, let's have a look at the respective directory:

	xd:>hadoop fs ls /count/out
	Found 2 items
	-rw-r--r--   3 hillert supergroup          0 2013-08-10 00:07 /count/out/_SUCCESS
	-rw-r--r--   3 hillert supergroup      31752 2013-08-10 00:07 /count/out/part-r-00000

Finally, executing:

	xd:>hadoop fs cat /count/out/part-r-00000

should yield a long list of words, indicating the number of occurrences within the provided input text.


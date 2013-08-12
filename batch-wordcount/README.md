Spring XD Batch Word-count Sample
=================================

This is the [*Spring Batch* word-count sample](https://github.com/SpringSource/spring-data-book/tree/master/hadoop/batch-wordcount) for Hadoop adapted for *Spring XD*. This sample will take an input file and counts the occurrences of each word within that document.

## Bulding

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
* hd.fs - The [Hadoop NameNode](http://wiki.apache.org/hadoop/NameNode) to use 

As an example, the data file `nietzsche-chapter-1.txt` is provided. Please change property `local.data.file`, so that its value points to the absolute location of `nietzsche-chapter-1.txt`.

## Running the Sample

1. Copy the **wordcount-context.xml** file to your *Spring XD* home directory under `modules/job`.
2. From directory `` copy the dependency `hadoop-examples-1.1.2.jar` to the `lib/` directory inside your *Spring XD* home directory.

Now your Sample is ready to be executed. Start your *Spring XD* admin server (If it was already running, you must restart it).
You will now create a new Batch Job Stream using the *Spring XD Shell*:

    xd:>job create --name wordCountJob --definition "wordcount"

You should see a message:

    Successfully created and deployed job 'wordCountJob'

## Verify the result

	xd:>hadoop config fs --namenode hdfs://localhost:8020
	xd:>hadoop fs ls /

You should see output like the following:

	Found 4 items
	drwxr-xr-x   - hillert supergroup          0 2013-08-06 22:35 /Users
	drwxr-xr-x   - hillert supergroup          0 2013-08-12 11:01 /count
	drwxr-xr-x   - hillert supergroup          0 2013-08-09 11:31 /user
	drwxr-xr-x   - hillert supergroup          0 2013-08-08 10:53 /xd

xd:>hadoop fs ls /count/out

	Found 2 items
	-rw-r--r--   3 hillert supergroup          0 2013-08-10 00:07 /count/out/_SUCCESS
	-rw-r--r--   3 hillert supergroup      31752 2013-08-10 00:07 /count/out/part-r-00000

Finally, executing:

	xd:>hadoop fs cat /count/out/part-r-00000

should yield a long list of words, indicating the number of occurrences within the provided input text.

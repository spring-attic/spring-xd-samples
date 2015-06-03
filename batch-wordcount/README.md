Spring XD Batch Word-count Sample
=================================

This is the [*Spring Batch* word-count sample](https://github.com/SpringSource/spring-data-book/tree/master/hadoop/batch-wordcount) for Hadoop adapted for *Spring XD*. This sample will take an input file and counts the occurrences of each word within that document.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))
* Hadoop ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Hadoop-Installation))

## Building

Before building, please verify the setting under `/src/main/resources/config/wordcount.xml`. It defines the location of the file to import, HDFS directories to use as well as the name node location. Please check the the settings under in `util:property` element:

	<util:properties id="myProperties" >
		<prop key="wordcount.input.path">/count/in/</prop>
		<prop key="wordcount.output.path">/count/out/</prop>
		<prop key="hd.fs">hdfs://localhost:8020</prop>
	</util:properties>

Please verify particularly the following property:

* hd.fs - The [Hadoop NameNode](http://wiki.apache.org/hadoop/NameNode) to use. The setting should be fine, but the port may be different between Hadoop versions (e.g. port 9000 is common also)

Now you can build the sample simply by executing:

	$ mvn clean package

The project [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to compile and test the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies that are not already provided by the Spring XD container. In this case there are no additional dependencies so the artifact is built as a common jar. ee the [Modules][] section in the Spring XD Reference for more details on module packaging.

As a result, you will see the following jar being created: `target/batch-wordcount-1.0.0.BUILD-SNAPSHOT.jar`.

## Running the Sample

The wordcount sample is ready to be executed. The simplest way to run Spring XD is using the `singlenode` server.

	xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

## Upload the module

In the Spring XD shell:

    xd:>module upload --type job --name wordcount --file [path-to]/batch-wordcount-1.0.0.BUILD-SNAPSHOT.jar

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create --name wordCountJob --definition "wordcount"

The UI located on the machine where `xd-singlenode` is running, will show you the jobs that can be deployed.  The UI is located at:

http://localhost:9393/admin-ui

Alternatively, you can deploy the job using the shell command:

	xd:>job deploy --name wordCountJob

We will now create a stream that polls a local directory for files.  By default the name of the directory is named after the name of the stream, so in this case the directory will be `/tmp/xd/input/wordCountFiles`. If the directory does not exist, it will be created. You can override the default directory using the `--dir` option.

	xd:>stream create --name wordCountFiles --definition "file --mode=ref > queue:job:wordCountJob" --deploy

If you now drop text files into the  `/tmp/xd/input/wordCountFiles/` directory, the file will be picked up, copied to HDFS and its words counted. You can move the supplied `nietzsche-chapter-1.txt` file to the input directory using the shell by executing:

	xd:>! cp /path/to/spring-xd-samples/batch-wordcount/data/nietzsche-chapter-1.txt /tmp/xd/input/wordCountFiles

**Important**: If you have an empty `/count/in` directory on the *hdfs* (check with `xd:> hadoop fs ls /`), remove it using `xd:> hadoop fs rm /count --recursive` before copying files to `/tmp/xd/input/wordCountFiles`. 

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

As we declared the property `wordcount.output.path` in **wordcount.xml** to be `/count/out/`, let's have a look at the respective directory:

	xd:>hadoop fs ls /count/out
	Found 2 items
	-rw-r--r--   3 hillert supergroup          0 2013-08-10 00:07 /count/out/_SUCCESS
	-rw-r--r--   3 hillert supergroup      31752 2013-08-10 00:07 /count/out/part-r-00000

Finally, executing:

	xd:>hadoop fs cat /count/out/part-r-00000

should yield a long list of words, indicating the number of occurrences within the provided input text.

[pom]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-wordcount/pom.xml
[Spring Boot Maven Plugin]: http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
[Modules]: http://docs.spring.io/spring-xd/docs/current/reference/html/#modules
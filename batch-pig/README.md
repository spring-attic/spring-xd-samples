Spring XD Batch Pig Top 10 Hashtag Count Sample
===============================================

This sample will take an input file with Twitter hashtags with corresponding counts and extract the top 10 hashtags based on the counts. 


## Requirements

In order for the sample to run you will need to have installed:

* Spring XD 1.1.0.RELEASE or later ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))
* Hadoop ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Hadoop-Installation))

If you ran the `batch-hashtag-count` sample then you already have the file we need for the input. If not, then you can copy the provided sample file before running this sample using the following commands in the XD Shell.

First, configure the XD Shell to use our Hadoop NameNode:

	xd:>hadoop config fs --namenode hdfs://localhost:8020
	
Now we can copy the data file to the `/xd/hashtagcounts/output` directory.

    xd:>hadoop fs mkdir /xd/hashtagcount
    xd:>hadoop fs mkdir /xd/hashtagcount/output
    xd:>hadoop fs copyFromLocal --from <path-to-this-sample>/data/part-r-00000 --to /xd/hashtagcount/output/part-r-00000

> *NOTE:* If you are using an Hadoop cluster that uses a different classpath configuration than the default one from Apache Hadoop, then you need
> to provide a 'yarn.application.classpath' property with the correct classpath to be used by any task submitted to the YARN cluster. The file 
> `yarn-site.xml` that is included in this example should be used for this, see notes in this file for details.


## Building

Build the sample simply by executing:

	$ mvn package

The project [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to compile and test the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies that are not already provided by the Spring XD container. In this case there are no additional dependencies so the artifact is built as a common jar. See the [Modules][] section in the Spring XD Reference for more details on module packaging.

The modules `src\main\resources\config` directory contains the `spring-module.xml` file that defines the location of the input and output directories for the job. You can verify the settings inside `spring-module.xml`.  All relevant properties are defined in the `util:property` element:

    <util:properties id="myProperties" >
        <prop key="pig.input.path">/xd/hashtagcount/output</prop>
        <prop key="pig.output.path">/xd/pigout/results</prop>
    </util:properties>

The same `spring-module.xml` file also defines the Hadoop configuration, defaulting to what is specified in the Spring XD configuration.

    <hadoop:configuration>
        fs.defaultFS=${spring.hadoop.fsUri}
        yarn.resourcemanager.hostname=${spring.hadoop.resourceManagerHost}
        mapreduce.framework.name=yarn
        mapreduce.jobhistory.address=${spring.hadoop.resourceManagerHost}:10020
    </hadoop:configuration>


## Running the Sample

Now your sample is ready to be executed.  The simplest way to run Spring XD is using the singlenode server.

	xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

## Upload the module 

In the Spring XD shell:

    xd:>module upload --type job --name pigExample --file <path-to-this-sample>/target/batch-pig-1.0.0.BUILD-SNAPSHOT.jar


## Create the Batch Job

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create pigJob --definition "pigExample" --deploy

You should see a message:

	Successfully created and deployed job 'pigJob'

Launch the job using:

	xd:>job launch pigJob

You should see a message:

	Successfully submitted launch request for job 'pigJob'


## Verify the result

Check that the job is running and wait for it complete:

    xd:>job execution list
      Id  Job Name  Start Time               Step Execution Count  Execution Status  Deployment Status  Definition Status
      --  --------  -----------------------  --------------------  ----------------  -----------------  -----------------
      72  pigJob    2015-02-03 16:20:52,743  2                     STARTED           Deployed           Exists

When the Execution Status changes to COMPLETED you can check the results.

To do that we should configure the XD Shell to use our Hadoop NameNode:

	xd:>hadoop config fs --namenode hdfs://localhost:8020
	
We will now take a look at the results in the *HDFS* filesystem:
	
	xd:>hadoop fs ls /xd/pigout/results
    Hadoop configuration changed, re-initializing shell...
    Found 2 items
    -rw-r--r--   3 trisberg supergroup          0 2015-02-03 16:21 /xd/pigout/results/_SUCCESS
    -rw-r--r--   3 trisberg supergroup        235 2015-02-03 16:21 /xd/pigout/results/part-r-00000

Finally, executing:

	xd:>hadoop fs cat /xd/pigout/results/part-r-00000

should yield a list of 10 hashtags and their respective counts.

[pom]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-pig/pom.xml
[Spring Boot Maven Plugin]: http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
[Modules]: http://docs.spring.io/spring-xd/docs/current/reference/html/#modules

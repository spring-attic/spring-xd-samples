Spring XD Batch Hive Twitter Influencers Sample
===============================================

This sample will take an input file with Twitter tweets and use the followers attribute to rank the users tweeting. The assumption is that the accounts with the most followers are more influential


## Requirements

In order for the sample to run you will need to have installed:

* Spring XD 1.1.0.RELEASE or later ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))
* Hadoop ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Hadoop-Installation))

If you ran the `batch-hashtag-count` sample then you already have the tweet files we need for the input. If not, then you can copy the provided sample file before running this sample using the following commands in the XD Shell.

First, configure the XD Shell to use our Hadoop NameNode:

	xd:>hadoop config fs --namenode hdfs://localhost:8020
	
Now we can copy the data file to the `/xd/tweets` directory.

    xd:>hadoop fs mkdir /xd/tweets
    xd:>hadoop fs copyFromLocal --from <path-to-this-sample>/data/tweets-0.txt --to /xd/tweets/tweets-0.txt


## Building

Build the sample simply by executing:

	$ mvn package

The project [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to compile and test the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies that are not already provided by the Spring XD container. In this case there are no additional dependencies so the artifact is built as a common jar. See the [Modules][] section in the Spring XD Reference for more details on module packaging.

The modules `src\main\resources\config` directory contains the `spring-module.xml` file that defines the location of the input and output directories for the job. You can verify the settings inside `spring-module.xml`.  All relevant properties are defined in the `util:property` element:

    <util:properties id="myProperties" >
        <prop key="hive.input.path">/xd/tweets</prop>
        <prop key="hive.output.path">/xd/hiveout</prop>
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

    xd:>module upload --type job --name hiveExample --file <path-to-this-sample>/target/batch-hive-1.0.0.BUILD-SNAPSHOT.jar


## Create the Batch Job

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create hiveJob --definition "hiveExample" --deploy

You should see a message:

	Successfully created and deployed job 'hiveJob'

Launch the job using:

	xd:>job launch hiveJob

You should see a message:

	Successfully submitted launch request for job 'hiveJob'


## Verify the result

Check that the job is running and wait for it complete:

    xd:>job execution list
      Id  Job Name  Start Time               Step Execution Count  Execution Status  Deployment Status  Definition Status
      --  --------  -----------------------  --------------------  ----------------  -----------------  -----------------
      72  hiveJob   2015-02-04 11:21:56,778  2                     STARTED           Deployed           Exists

When the Execution Status changes to COMPLETED you can check the results.

To do that we should configure the XD Shell to use our Hadoop NameNode:

	xd:>hadoop config fs --namenode hdfs://localhost:8020
	
We will now take a look at the results in the *HDFS* filesystem:
	
	xd:>hadoop fs ls /xd/hiveout
    Hadoop configuration changed, re-initializing shell...
    Found 1 items
    -rw-r--r--   3 trisberg supergroup        192 2015-02-04 11:32 /xd/hiveout/000000_0

Finally, executing:

	xd:>hadoop fs cat /xd/hiveout/000000_0

should yield a list of 10 Twitter users and their respective follower counts.

## Removing output directory

Depending on your cluster setup, the output directory might have been created by another user like `hive`. You would need to
remove this directory running as that user or with a super user account. On a non a non-secured cluster this can be achieved
with:

    $ HADOOP_USER_NAME=hive hadoop fs -rm -r /xd/hiveout

[pom]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-hive/pom.xml
[Spring Boot Maven Plugin]: http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
[Modules]: http://docs.spring.io/spring-xd/docs/current/reference/html/#modules

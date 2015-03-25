Spring XD Simple Batch Sample
=============================

In this *Hello World* example for *Spring XD* you will create a minimal code-based Job. This Job does no processing, only printing out `Hello Spring XD!` as well as any Job parameters that were passed into the Job so as to focus only on the mechanics of the compilation and copying of artifacts into the Spring XD installation directory.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD 1.1.0.RELEASE or higher ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))

## Code Tour

The processing actions that are part of a Step in a batch Job are pluggable.  The plug-in point for a Step is known as a [Tasklet](http://static.springsource.org/spring-batch/apidocs/org/springframework/batch/core/step/tasklet/Tasklet.html).  In this example we create a tasklet by implementing the Tasklet interface.  Take a look at the [source code](https://github.com/spring-projects/spring-xd-samples/blob/master/batch-simple/src/main/java/org/springframework/springxd/samples/batch/HelloSpringXDTasklet.java) as well as its incorporation into a [Job definition](https://github.com/spring-projects/spring-xd-samples/blob/master/batch-simple/src/main/resources/spring-module.xml) inside an XML file. Note that the XML file must contain a single Job.

## Building with Maven

Build the sample simply by executing:

	$ mvn clean package

The project's [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to compile and test the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies that are not already provided by the Spring XD container. In this case there are no additional dependencies so the artifact is built as a common jar. See the [Modules][] section in the Spring XD Reference for more details on module packaging.

## Building with Gradle

	$./gradlew clean bootRepackage

The project's [build.gradle][] applies the `spring-xd-module` plugin, providing analagous build and packaging support for gradle. This plugin also applies the [Spring Boot Gradle Plugin][] as well as the [propdeps plugin][]. 


## Running the Sample

Now your sample is ready to be executed. Start your *Spring XD* singlenode server:

	xd/bin>$ ./xd-singlenode

	 _____                           __   _______
	/  ___|          (-)             \ \ / /  _  \
	\ `--. _ __  _ __ _ _ __   __ _   \ V /| | | |
	 `--. \ '_ \| '__| | '_ \ / _` |  / ^ \| | | |
	/\__/ / |_) | |  | | | | | (_| | / / \ \ |/ /
	\____/| .__/|_|  |_|_| |_|\__, | \/   \/___/
	      | |                  __/ |
	      |_|                 |___/
	1.1.0.BUILD-SNAPSHOT             eXtreme Data


	Started container : SingleNodeApplication
	Documentation: https://github.com/SpringSource/spring-xd/wiki
	...

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

	 _____                           __   _______
	/  ___|          (-)             \ \ / /  _  \
	\ `--. _ __  _ __ _ _ __   __ _   \ V /| | | |
	 `--. \ '_ \| '__| | '_ \ / _` |  / ^ \| | | |
	/\__/ / |_) | |  | | | | | (_| | / / \ \ |/ /
	\____/| .__/|_|  |_|_| |_|\__, | \/   \/___/
	      | |                  __/ |
	      |_|                 |___/
	eXtreme Data
	1.1.0.BUILD-SNAPSHOT | Admin Server Target: http://localhost:9393
	Welcome to the Spring XD shell. For assistance hit TAB or type "help".
	xd:>

First install the module using the `module upload` command:

	xd:>module upload --type job --name myjob --file [path-to]/springxd-batch-simple-1.0.0.BUILD-SNAPSHOT.jar

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create --name helloSpringXD --definition "myjob"

The UI is located on the machine where xd-singlenode is running and will show you the jobs that can be deployed. The UI is located at:

* **http://localhost:9393/admin-ui**

Alternatively, you can deploy it using the command line

	xd:>job deploy helloSpringXD

And then launch the job

	xd:>job launch helloSpringXD

You should see a message in the log output from the XD container:

	Hello Spring XD!
	The following 1 Job Parameter(s) is/are present:
	Parameter name: random; isIdentifying: true; type: STRING; value: 0.07119643877192872

You can also experiment with Job parameters:

	job launch helloSpringXD --params {"myStringParameter":"foobar","-secondParam(long)":"123456"}

	Hello Spring XD!
	The following 3 Job Parameter(s) is/are present:
	Parameter name: secondParam; isIdentifying: false; type: LONG; value: 123456
	Parameter name: myStringParameter; isIdentifying: true; type: STRING; value: foobar
	Parameter name: random; isIdentifying: true; type: STRING; value: 0.06893349621991496
	12:04:18,384  INFO http-nio-9393-exec-5 support.SimpleJobLauncher:135 - Job: [FlowJob: [name=helloSpringXD.job]] completed with the following parameters: [{secondParam=123456, myStringParameter=foobar, random=0.06893349621991496}] and the following status: [COMPLETED]

## Throwing Exceptions

You can trigger an exception by providing a parameter named `throwError` with a String value of `true`.

## Adding Variables to the Step Execution Context

Any parameters that start with `context` will be added to the Step Execution Context.
E.g. if you add a parameter named `contextHello` with a String value of `World`,
the variable `contextHello` will be added to the Step Execution Context. You can
verify the context using the **Admin UI** and drilling to the Step Execution Details
via the *Executions* tab.

## Job Repository

In this example the state of the Job execution is stored in an HSQLDB database embedded inside the single node server. Please refer to the Spring XD documentation if you would like to store this data in another database.

## To run batch job with multiple steps

Follow the instructions in the module's [xml] configuration to have batch job with multiple steps

[xml]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-simple/src/main/resources/config/spring-module.xml
[pom]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-simple/pom.xml
[build.gradle]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-simple/build.gradle
[Spring Boot Maven Plugin]: http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
[Spring Boot Gradle Plugin]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/build-tool-plugins-gradle-plugin.html
[propdeps plugin]: https://github.com/spring-projects/gradle-plugins/tree/master/propdeps-plugin
[Modules]: http://docs.spring.io/spring-xd/docs/current/reference/html/#modules

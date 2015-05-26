Spring XD Nested Job Sample
=============================

In this example of *Spring Batch* for *Spring XD* you will create a nested job module.  Neither job in this example does any real processing.  Instead the focus is on the structure of a job module that contains other jobs.  This use case is useful for things like orchestrating jobs and other workflows.


## Requirements

In order for the sample to run you will need to have installed:

* Spring XD 1.2.0.RELEASE or higher ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))

## Code Tour

In this example we use Spring Batch's `JobStep` to execute a child job.  While all of the configuration is located within the single `NestedJobsConfiguration` class, you can include jobs from other classes via regular Spring conventions (`@Import` for example).

This example uses Spring's java configuration instead of XML.  To tell Spring XD where to look for the configuration, the package name of where scanning should begin is defined in the properties file (`/src/main/resources/batch-nested.properties`).

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
	1.2.0.BUILD-SNAPSHOT             eXtreme Data


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
	1.2.0.BUILD-SNAPSHOT | Admin Server Target: http://localhost:9393
	Welcome to the Spring XD shell. For assistance hit TAB or type "help".
	xd:>

First install the module using the `module upload` command:

	xd:>module upload --type job --name nestedJob --file [path-to]/springxd-batch-nested-1.0.0.BUILD-SNAPSHOT.jar

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create --name helloSpringXD --definition "nestedJob"

The UI is located on the machine where xd-singlenode is running and will show you the jobs that can be deployed. The UI is located at:

* **http://localhost:9393/admin-ui**

Alternatively, you can deploy it using the command line

	xd:>job deploy helloSpringXD

And then launch the job

	xd:>job launch helloSpringXD

You should see a message in the log output from the XD container:

	Hello Spring XD!

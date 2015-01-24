Spring XD Payments Import with Notifications
============================================

This sample will take an input file containing payment data, and import the data into a database using Spring XD's batch job support. During import various notification events are triggered and printed to the console.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD 1.1.0.M2 or later ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))

## Building with Maven

Build the sample simply by executing:

	$ mvn clean package

The project [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to compile and test the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies that are not already provided by the Spring XD container. In this case there are no additional dependencies so the artifact is built as a common jar. See the [Modules][] section in the Spring XD Reference for more details on module packaging.

## Building with Gradle

	$./gradlew clean bootRepackage

The project's [build.gradle][] applies the `spring-xd-module` plugin, providing analagous build and packaging support for gradle. This plugin also applies the [Spring Boot Gradle Plugin][] as well as the [propdeps plugin][]. 

## Running the Sample

Now your Sample is ready to be executed. Start your *Spring XD* single node server:
	xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

## Install the job

	xd:>module upload --type job --name payment-import --file [path-to]/batch-notifications-1.0.0.BUILD-SNAPSHOT.jar

## Setup the process

In this example, the job is driven by a stream rather than being launched using a separate command. A job instance is launched by posting http data to it.

	xd:> job create --name payment --definition "payment-import --makeUnique=false" --deploy 
	xd:> stream create --name paymenthttp --definition "http > queue:job:payment" --deploy
	xd:> stream create --name paymenttap --definition "tap:job:payment > log" --deploy
	
We also create a separate stream which sends notifications from the job to the log. This lets us know when the job completes, along with status information.

## Execute the process

In the `batch-notifications` directory

	$ ./copy-data.sh

to create the `/tmp/payment.input` file. The payment file is located under `/data/paymentImport/payment.input` and was copied to your `/tmp` directory.

	xd:> http post --data {"input.file.name":"/tmp/payment.input"}
	
## Results

You will see 27 payments being imported. The notifications are printed to the console. If you try to execute the batch job again, you will notice that the batch job will not execute again, as it was already run. In that case you should see an exception such as:

	JobInstanceAlreadyCompleteException:
	A job instance already exists and is complete for parameters={input.file.name=/path/to/payment.txt}.  If you want to run this job again, change the parameters.

If you would leave off the parameter `--makeUnique=false` when creating a job, you could run the job over and over again. Alternatively, could also just change the name of the input file, which constitutes a change in the submitted parameters.

## Trigger error behavior and retry

Let try to import a payment file that contains an error. E.g.:

	...
	1,2,2.0,2010-01-14
	1,2,2.0,2010-01-15
	1,2,2.0,2010xxx-16
	1,2,2.0,2010-01-17
	1,2,2.0,2010-01-18
	...

Executed the process with:

	xd:> http post --data {"input.file.name":"/path/to/payment_with_error.txt"}

The import will fail but all the payment records up to erroneous row have been imported successfully. Now lets fix that row save the file and execute again:

	xd:> http post --data {"input.file.name":"/path/to/payment_with_error.txt"}
	
This time the import will continue with the previously erroneous row and continues with the successful import of the rest of the file.

[xml]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-notifications/src/main/resources/config/spring-module.xml
[pom]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-notifications/pom.xml
[build.gradle]: https://github.com/spring-projects/spring-xd-samples/blob/master/batch-notifications/build.gradle
[Spring Boot Maven Plugin]: http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
[Spring Boot Gradle Plugin]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/build-tool-plugins-gradle-plugin.html
[propdeps plugin]: https://github.com/spring-projects/gradle-plugins/tree/master/propdeps-plugin
[Modules]: http://docs.spring.io/spring-xd/docs/current/reference/html/#modules

Spring XD Custom Groovy Sink
=============================

This is an example of a custom module project that is built and packaged for installation in a Spring XD runtime environment using maven. 
This illustrates how to bind variables defined as module options to a groovy script.

## Requirements

In order to install the module and run it in your Spring XD installation, you will need to have installed:

* Spring XD version 1.1.x ([Instructions](http://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started))

## Code Tour

This implements a simple custom module which simply appends values `${foo}` and `${bar}` to a payload and prints the result to `stdout`. 
The example demonstrates the use of the `spring-xd-module-parent` pom to package the module.

## Building

	$ mvn package

The project [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to compile and test 
the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies 
that are not already provided by the Spring XD container. 

In this case, `spring-xd-extension-script` is a module dependency that must be packaged with the module to be loaded by the module's class loader.
 This component has transitive dependencies to support Spring Integration Groovy scripts which are also exported to the uber-jar

## Using the Custom Module

The uber-jar will be in `target/groovy-script-sink-1.0.0.BUILD-SNAPSHOT.jar`. To install and register the module to your Spring XD distribution,
 use the `module upload` Spring XD shell command. Start Spring XD and the shell:


	_____                           __   _______
	/  ___|          (-)             \ \ / /  _  \
	\ `--. _ __  _ __ _ _ __   __ _   \ V /| | | |
 	`--. \ '_ \| '__| | '_ \ / _` |   / ^ \| | | |
	/\__/ / |_) | |  | | | | | (_| | / / \ \ |/ /
	\____/| .__/|_|  |_|_| |_|\__, | \/   \/___/
    	  | |                  __/ |
      	|_|                 |___/
	eXtreme Data
	1.1.0.BUILD-SNAPSHOT | Admin Server Target: http://localhost:9393
	Welcome to the Spring XD shell. For assistance hit TAB or type "help".
	xd:>module upload --type sink --name groovySink --file [path-to]/spring-xd-samples/groovy-script-sink/target/groovy-script-sink-1.0.0.BUILD-SNAPSHOT.jar
	Successfully uploaded module 'sink:groovySink'
	xd:>


Now create and deploy a stream:

	stream create test --definition "time | groovySink --variables='foo=fooValue,bar=barValue'" --deploy


You should see the stream output in the Spring XD log:


	2015-01-22 11:28:02:fooValue:barValue
    2015-01-22 11:28:03:fooValue:barValue
    2015-01-22 11:28:04:fooValue:barValue


[pom]: https://github.com/spring-projects/spring-xd-samples/blob/master/groovy-script-sink/pom.xml
[Spring Boot Maven Plugin]: http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
[Modules]: http://docs.spring.io/spring-xd/docs/current/reference/html/#modules

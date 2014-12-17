Spring XD SI DSL Module
=============================

This is an example of a custom module project that can be built and packaged for installation in an Spring XD runtime environment using maven. The project includes sample unit and integration tests, including the ability to test the module in an embedded single node container. It also illustrates how to define module options which are bound to either Spring properties or environment profiles.

## Requirements

In order to install the module run it in your Spring XD installation, you will need to have installed:

* Spring XD version 1.1.x ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))

## Code Tour

This implements a simple custom module which simply adds a prefix and/or suffix to a string payload. The example demonstrates the use of the spring-xd-module-parent pom and is intentionally over-engineered to demonstrate Spring XD support for module development including:
 * Advanced features related to  module options, such as using a Java class to define and validate module option values, and how to bind a module option to a Spring environment profile. 
 * The use of Spring Java configuration (@Configuration) and the [Spring Integration Java DSL][] for implementing the payload transformation
 * A unit test to validate the Module Options Metadata
 * An integration test to test the module definition with Spring Integration
 * An integration test to test the module registered and used in an embedded Spring XD single node container 


## Building

	$ mvn package

The project [pom][] declares 'spring-xd-module-parent' as its parent. This adds the dependencies needed to compile and test the module and also provides and configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar packaging any dependencies that are not already provided by the Spring XD container. 


## Using the Custom Module

The uber-jar will be in `target/si-dsl-module-1.0.0.BUILD-SNAPSHOT.jar`. To install and register the module to your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:


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
	xd:>module upload --file [path-to]/spring-xd-samples/si-dsl-module/target/si-dsl-module-1.0.0.BUILD-SNAPSHOT.jar --name si-dsl-module --type processor
	Successfully uploaded module 'processor:si-dsl-module'
	xd:>


You can also get information about the available module options:

	xd:>module info processor:si-dsl-module
	Information about processor module 'si-dsl-module':

  	Option Name  Description                                            Default  Type
  	-----------  -----------------------------------------------------  -------  --------
  	prefix       the prefix                                             <none>   String
  	prefixOnly   set to true to prepend prefix only                     false    boolean
  	suffix       the suffix                                             <none>   String
  	outputType   how this module should emit messages it produces       <none>   MimeType
  	inputType    how this module should interpret messages it consumes  <none>   MimeType


Now create and deploy a stream:

	xd:>stream create test --definition "http |si-dsl-module --prefix='just saying ' --suffix=', world' | log" --deploy

Post some data:

	xd:>http post --target http://localhost:9000 --data hello
	> POST (text/plain;Charset=UTF-8) http://localhost:9000 hello
	> 200 OK


You should see the stream output in the Spring XD log:


	14:11:19,513 1.1.0.SNAP  INFO DeploymentSupervisor-0 server.StreamDeploymentListener - Stream Stream{name='test'} deployment attempt complete
	14:11:22,582 1.1.0.SNAP  INFO pool-10-thread-4 sink.test - just saying hello, world


[Spring Integration Java DSL]: https://github.com/spring-projects/spring-integration-java-dsl
[Spring Boot Maven Plugin] : http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
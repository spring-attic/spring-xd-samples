Redis Store Sink Module
=======================

This is an example of a custom sink module using the Spring Integration Redis outbound channel adapter to save messages to a Redis collection. This is built and packaged for installation in a Spring XD runtime environment using maven. The project includes unit and integration tests and illustrates how to define module options using simple property descriptors.

## Requirements

In order to install the module and run it in your Spring XD installation, you will need to have installed:

* Spring XD version 1.1.x ([Instructions](http://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started))

## Code Tour

This implements a Spring XD sink module using an existing Spring Integration outbound channel adapter. A simple integration test sends a message to the module's `input` channel and verifies the message payload is saved to the specified Redis collection.

## Building with Maven

	$ mvn clean package

The project's [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to test the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies that are not already provided by the Spring XD container. In this case, the uber-jar must export `spring-integration-redis` since it is not a native Spring XD dependency. See the [Modules][] section in the Spring XD Reference for a more detailed explanation of module class loading. 

## Building with Gradle

	$./gradlew clean test bootRepackage

The project's [build.gradle][] applies the `spring-xd-module` plugin, providing analagous build and packaging support for gradle. This plugin also applies the [Spring Boot Gradle Plugin][] as well as the [propdeps plugin][]. 

## Using the Custom Module

The jar will be in `[project-build-dir]/redis-store-sink-1.0.0.BUILD-SNAPSHOT.jar`. To install and register the module to your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:


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
	xd:>module upload --file [path-to]/redis-store-sink-1.0.0.BUILD-SNAPSHOT.jar --name redis-store --type sink
	Successfully uploaded module 'sink:redis-store'
	xd:>

You can also get information about the available module options:

	xd:>module info sink:redis-store
	
	Information about sink module 'redis-store':

  	Option Name  Description                                            Default            Type
  	-----------  -----------------------------------------------------  -----------------  --------
  	host         the host name for the Redis store                      localhost          String
  	port         the port for the Redis store                           6379               Integer
  	collection   the name of the list                                   ${xd.stream.name}  String
  	inputType    how this module should interpret messages it consumes  <none>             MimeType



Now create and deploy a stream:

	xd:>stream create redisTest --definition "http | redis-store" --deploy

and post some data to the stream:

	xd:>http post --target http://localhost:9000 --data hello

You can verify the data is stored using the Redis CLI:

	$ redis cli
	127.0.0.1:6379> lrange redisTest 0 -1
	1) "hello"

[pom]: https://github.com/spring-projects/spring-xd-samples/blob/master/redis-store-sink/pom.xml
[build.gradle]: https://github.com/spring-projects/spring-xd-samples/blob/master/redis-store-sink/build.gradle
[Spring Boot Maven Plugin]: http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
[Spring Boot Gradle Plugin]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/build-tool-plugins-gradle-plugin.html
[propdeps plugin]: https://github.com/spring-projects/gradle-plugins/tree/master/propdeps-plugin
[Modules]: http://docs.spring.io/spring-xd/docs/current/reference/html/#modules
Tweet Transformer Processor
=============================

This is an example of a custom module project that can be built and packaged as a simple jar file for installation in a Spring XD runtime environment using maven. The project includes sample integration tests demonstrating the ability to test the module in an embedded single node container.

## Requirements

In order to install the module and run it in your Spring XD installation, you will need to have installed:

* Spring XD ([Instructions](http://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started))

## Code Tour

This implements a simple custom module which extracts the `text` property from a tweet rendered as JSON, as produced by the `twittersearch` source. Note that Spring XD can accomplish the same functionality without a custom jar. The example demonstrates how to build a module as a simple jar with maven or gradle.


## Building

	$ mvn package

or

	$./gradlew test jar


## Using the Custom Module

The jar will be in `target/tweet-transformer-1.0.0.BUILD-SNAPSHOT.jar` (maven) or `build/libs/tweet-transformer-1.0.0.BUILD-SNAPSHOT.jar` (gradle). To install and register the module to your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:


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
	xd:>module upload --file [path-to]/spring-xd-samples/tweet-transformer-processor/[build-dir]/tweet-transformer-1.0.0.BUILD-SNAPSHOT.jar --name tweet-transformer --type processor
	Successfully uploaded module 'processor:tweet-transformer'
	xd:>


Now create and deploy a stream:

Note: You must provide twitter credentials to run this example. See the [twittersearch source](http://docs.spring.io/spring-xd/docs/current/reference/html/#twitter-search) documentation for details.

	xd:>stream create test --definition "twittersearch --query=[querystring] | tweet-transformer | log" --deploy


You should see the text of the tweets output in the Spring XD log.


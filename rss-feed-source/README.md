RSS Feed Source Module
=============================

This is an example of a custom source module using the Spring Integration feed inbound channel adapter to stream RSS Feeds (`spring-integration-feed`). This is built and packaged for installation in a Spring XD runtime environment using maven. The project includes sample unit and integration tests, including the ability to test the module in an embedded single node container. It also illustrates how to define module options using simple property descriptors.

## Requirements

In order to install the module and run it in your Spring XD installation, you will need to have installed:

* Spring XD version 1.1.x ([Instructions](http://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started))

## Code Tour

This implements a source module which streams an RSS feed from a given URL using an existing Spring Integration inbound channel adapter. The example demonstrates the use of the `spring-xd-module-parent` pom and an integration test to test the module registered and deployed in an embedded Spring XD single node container.

The `spring-integration-feed` inbound channel adapter uses the [rome][] library and emits messages with a payload of the type `com.rometools.rome.feed.synd.SyndEntry`. This presents a common design challenge which module developers must address. SyndEntry is defined in the rome jar, packaged with the module, and loaded by the module's classloader when the module is deployed. However, this class is not visible by default to downstream modules. For this reason, it is generally not advisable to implement modules that emit domain types. 

This issue may be resolved in one of the following ways:

1) Use Spring XD's automatic type conversion to render the object as JSON:

    feed --outputType=application/json | log

2) Copy the jar containing the payload type, along with any dependencies, to the `xd/lib` folder where Spring XD is installed. NOTE: This works for development, but is not recommended since the jar must be manually installed in every node in the Spring XD cluster, and re-installed whenever Spring XD is upgraded (This is the situation as of Spring XD v1.1; improvements in this area are under consideration for a future release)

3) Provide a transformer in the module to perform the desired conversion

It turns out that Option 1 does not work in this case because one of SyndEntry's fields contains a circular reference which results in a stack overflow when invoking the Jackson ObjectMapper to convert the object to JSON. So Option 3 is the best choice. The module uses [SyndEntryJsonTransformer][] to configure an ObjectMapper that ignores the field containing the cycle. The ObjectMapper is used to write the feed contents to a JSON String.

## Building with Maven

	$ mvn package

The project's [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to test the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies that are not already provided by the Spring XD container. See the [Modules][] section in the Spring XD Reference for a more detailed explanation of module class loading.

## Building with Gradle

	$./gradlew clean test bootRepackage

The project's [build.gradle][] applies the `spring-xd-module` plugin, providing analagous build and packaging support for gradle. This plugin also applies the [Spring Boot Gradle Plugin][] as well as the [propdeps plugin][]. 

## Using the Custom Module

The uber-jar will be in `[project-build-dir]/rss-source-feed-1.0.0.BUILD-SNAPSHOT.jar`. To install and register the module to your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:


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
	xd:>module upload --file [path-to]/rss-source-feed-1.0.0.BUILD-SNAPSHOT.jar --name feed --type source
	Successfully uploaded module 'source:feed'
	xd:>


You can also get information about the available module options:

	xd:>module info source:feed

	Information about source module 'feed':

  	Option Name         Description                                                Default  Type
  	------------------  ---------------------------------------------------------  -------  ---------
  	fixedRate           the fixed rate polling interval specified in milliseconds  5000     int
  	url                 the URL of the RSS feed                                    <none>   java.lang.String
  	maxMessagesPerPoll  the maximum number of messages per poll                    100      int
  	outputType          how this module should emit messages it produces           <none>   MimeType


Now create and deploy a stream:

	xd:>stream create feedTest --definition "feed --url='http://feeds.bbci.co.uk/news/rss.xml' | log" --deploy


You should see the stream output in the Spring XD log with each entry rendered as JSON


[pom]: https://github.com/spring-projects/spring-xd-samples/blob/master/rss-feed-source/pom.xml
[build.gradle]: https://github.com/spring-projects/spring-xd-samples/blob/master/rss-feed-source/build.gradle
[Spring Boot Maven Plugin]: http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html
[Spring Boot Gradle Plugin]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/build-tool-plugins-gradle-plugin.html
[propdeps plugin]: https://github.com/spring-projects/gradle-plugins/tree/master/propdeps-plugin
[Modules]: http://docs.spring.io/spring-xd/docs/current/reference/html/#modules
[rome]: http://rometools.github.io/rome/
[SyndEntryJsonTransformer]:  https://github.com/spring-projects/spring-xd-samples/blob/master/rss-feed-source/src/main/java/com/acme/SyndEntryJsonTransformer.java

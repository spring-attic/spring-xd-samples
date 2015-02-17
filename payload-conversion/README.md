Payload Conversion
==================

This sample project implements a custom processor module named *myTupleProcessor*. When deployed to a Spring XD container, the sample demonstrates the use of the [Tuple][] data type. The module is backed by a java class *MyTupleProcessor* that currently returns the Tuple passed as an argument to its *process(Tuple tuple)* method. This code may be customized to do more interesting things as long as it returns a Tuple as a result.

This sample also illustrates XD's built in payload conversion. This module may be linked to a source that produces any JSON string.

## Building with Maven

	$ mvn package

The project's [pom][] declares `spring-xd-module-parent` as its parent. This adds the dependencies needed to test the module and also configures the [Spring Boot Maven Plugin][] to package the module as an uber-jar, packaging any dependencies that are not already provided by the Spring XD container. See the [Modules][] section in the Spring XD Reference for a more detailed explanation of module class loading.

## Building with Gradle

	$./gradlew clean test bootRepackage

The project's [build.gradle][] applies the `spring-xd-module` plugin, providing analagous build and packaging support for gradle. This plugin also applies the [Spring Boot Gradle Plugin][] as well as the [propdeps plugin][].

## Using the Custom Module

The uber-jar will be in `[project-build-dir]/payload-conversion-1.0.0.BUILD-SNAPSHOT.jar`. To install and register the module to your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:

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
    xd:>module upload --file [path-to]/payload-conversion-1.0.0.BUILD-SNAPSHOT.jar --name myTupleProcessor --type processor
    Successfully uploaded module 'processor:myTupleProcessor'

You can also get information about the available module options:

    xd:>module info processor:myTupleProcessor
    Information about processor module 'myTupleProcessor':

      Option Name  Description                                            Default  Type
      -----------  -----------------------------------------------------  -------  --------
      outputType   how this module should emit messages it produces       <none>   MimeType
      inputType    how this module should interpret messages it consumes  <none>   MimeType


### Running the example

 Create and deploy a stream:

	xd:>stream create test --definition "http | myTupleProcessor --inputType=application/x-xd-tuple | file" --deploy

Post some JSON content to the stream's http source:

	xd:>http post --target http://localhost:9000 --data {"symbol":"FAKE","price":75} --contentType "application/json"

You should see the Tuple rendered as JSON:
	
	xd:>! cat /tmp/xd/output/test.out
	
	{"id":"719f5276-22d2-434d-87dc-39a23a978077","timestamp":1376387174881,"symbol":"FAKE","price":"75"}

### What's Happening Under Hood

XD stream definitions support module parameters *inputType* and *outputType* and support certain type conversions out of the box. XD associates the content type *application/x-xd-tuple* with the Java type *org.springframework.xd.tuple.DefaultTuple* and can convert any valid JSON string to a tuple as requested. More details on XD type conversion can be found [here](https://github.com/spring-projects/spring-xd/wiki/Type-Conversion).
    
The conversion specified by the *inputType* parameter causes the JSON content to be converted to a Tuple which is what the MyTupleProcessor.process() method requires. The result of that method is piped to a file sink which renders the Tuple invoking its toString() method, which also happens to be JSON. Note the *id* and *timestamp* properties, indicating that the Tuple conversion was performed.   

[Tuple]: https://github.com/spring-projects/spring-xd/wiki/Tuples
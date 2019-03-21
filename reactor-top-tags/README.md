Spring XD Reactor Stream Example
================================

This is an example of a custom module that uses Reactor's Stream API.  

## Requirements

In order to install the module run it in your Spring XD installation, you will need to have installed:

* Spring XD version 1.1.x ([Instructions](https://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started)). You'll need to build Spring XD with Java 8+ to use this sample (which uses lambda expressions).

## Code Tour

The heart of the sample is the processing module named [TopTags.java](src/main/java/com/acme/TopTags.java). This uses the Stream API to perform an average over the last 5 values of data. The [Tuple](https://docs.spring.io/spring-xd/docs/current/reference/html/#tuples) data type is used as a generic container for keyed data.


## Building

	$ mvn package

## Using the Custom Module

The uber-jar will be in `target/reactor-top-tags-1.0.0.BUILD-SNAPSHOT.jar`. To install and register the module to your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:

```
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
xd:>module upload --file [path-to]/spring-xd-samples/reactor-top-tags/target/reactor-top-tags-1.0.0.BUILD-SNAPSHOT.jar --name reactor-top-tags --type processor
Successfully uploaded module 'processor:reactor-top-tags'
xd:>
```

Now create an deploy a stream:

```
xd:>stream create reactor --definition "http | reactor-top-tags --inputType=application/x-xd-tuple | log" --deploy
```

To post several messages, use the script file generateData.script located in this repository.

```
xd:>script --file [path-to]/generateData.script
```

This will post JSON data such as `{"id":"1","measurement":"10"}` with increasing valuespwd for the measurement. The use of the inputType option (all modules have this option) instructs XD to convert the JSON string to an XD Tuple object before invoking the process method.

You should see the stream output in the Spring XD log:

```
17:17:25,064 1.1.0.SNAP INFO pool-10-thread-12 sink.test3 - {"id":"d5c9617b-4bac-3786-6559-6b0ab221496c","timestamp":1419027445042,"average":12.0}
17:17:25,137 1.1.0.SNAP INFO pool-10-thread-6 sink.test3 - {"id":"661aaab5-a14c-b63d-7e6d-c3329de2866a","timestamp":1419027445133,"average":17.0}
```

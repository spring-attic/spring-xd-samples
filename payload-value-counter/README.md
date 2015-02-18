Payload Value Counter Demo
==========================

In this example you will learn how to create a simple custom counter with Spring XD.

This counter is a variation of Spring XD's [field value counter](https://github.com/spring-projects/spring-xd/wiki/Analytics#field-value-counter). Unlike the original, it is not a field of the payload that sets the updated value, but the payload itself.

We will use a simple example to illustrate how it works.

Prerequisites
-------------

In order to get started, make sure that Spring XD is installed. You can find the instructions
([here](https://github.com/spring-projects/spring-xd/wiki/Getting-Started)).

## Building

	$ mvn package

## Using the Custom Module

The uber-jar will be in `target/payload-value-counter-1.0.0.BUILD-SNAPSHOT.jar`. To install and register the module to your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:

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
xd:>module upload --file [path-to]/spring-xd-samples/payload-value-counter/target/payload-value-counter-1.0.0.BUILD-SNAPSHOT.jar --name payload-value-counter --type sink
Successfully uploaded module 'sink:payload-value-counter'
xd:>
```

Standalone mode
---------------

Start Spring XD in standalone mode.

First, we create a normal ingestion stream, with the following modules:

* a file source that monitors a directory containing text files - whenever a new file is copied there,
the source will read it, each of the messages that it produces containing one line;
* a splitter that receives the lines of text produced by the source, and splits them into words, using
space as a separator (you can change the SpEL expression for more accuracy);
* a sink that logs the emitted words.

```
stream create words --definition "file --dir=<directory-with-text-files> --outputType=text/plain | splitter --expression=payload.split(' ')  | log
```

Then we will tap the original stream and create a new one, for counting the words. We will use a field value counter for accumulating results in the
* the `words` stream is tapped after the splitter;
* we use a transformer to wrap the individual words into Spring XD `Tuple`s, which are one of the expected inputs for the field value counter.


```
stream create wordcount --definition "tap:stream:words.splitter > payload-value-counter"
```

You can use the [analytics-dashboard](../analytics-dashboard) project to visualize the word counts as the stream is processed.

Distributed mode
----------------

In a distributed mode, the scenario is identical, except the module counts can be adjusted.


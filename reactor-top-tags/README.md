Spring XD Reactor Stream Example
================================

This is an example of a custom module that uses Reactor's Stream API.  

## Requirements

In order to install the module run it in your Spring XD installation, you will need to have installed:

* Spring XD version 1.1.x ([Instructions](http://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started)). You'll need to build Spring XD with Java 8+ to use this sample (which uses lambda expressions).

## Code Tour

The heart of the sample is the processing module named [TopTags.java](src/main/java/com/acme/TopTags.java).
This uses the Stream API to calculate the most referenced tags in a given time window. The[Tuple]
(http://docs.spring.io/spring-xd/docs/current/reference/html/#tuples) data type is used as a generic
container for keyed data.



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
xd:>stream create reactor --definition "tweetstream | reactor-top-tags | log" --deploy
```

You should see the stream output in the Spring XD log:

```
2015-02-16 21:15:34,530 1.1.0.RELEASE  INFO pool-13-thread-1 sink.toptweets - {"id":"49ae2d6c-7404-cb90-351a-80234b8d5b21","timestamp":1424139334512,"topTags":{"SNL40":18,"NBAAllStarNYC":4,"SpringXD":4}}
```

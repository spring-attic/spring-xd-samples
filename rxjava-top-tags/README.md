Spring XD Reactor Stream Example
================================

This is an example of a custom module that uses RxJava's Observable API.

## Requirements

In order to install the module run it in your Spring XD installation, you will need to have installed:

* Spring XD version 1.1.x ([Instructions](http://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started)). You'll need to build Spring XD with Java 8+ to use this sample (which uses lambda expressions).

## Code Tour

The heart of the sample is the processing module named [TopTags.java](src/main/java/com/acme/TopTags.java).
This uses the Observable API to calculate the most referenced tags in a given time window. The[Tuple](http://docs.spring.io/spring-xd/docs/current/reference/html/#tuples) data type is used as a generic container for keyed data.


## Building

	$ mvn package

## Using the Custom Module

The uber-jar will be in `target/rxjava-top-tags-1.0.0.BUILD-SNAPSHOT.jar`. To install and register the module to your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:

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
xd:>module upload --file [path-to]/spring-xd-samples/rxjava-top-tags/target/rxjava-top-tags-1.0.0.BUILD-SNAPSHOT.jar --name rxjava-top-tags --type processor
Successfully uploaded module 'processor:reactor-top-tags'
xd:>
```

Now create an deploy a stream:

```
xd:>stream create reactor --definition "tweetstream | rxjava-top-tags | log" --deploy
```

The `rxjava-top-tags` processor also supports the `timeWindow` and `topN` parameters for customizing the processor's
behavior.

You should see the stream output in the Spring XD log, indicating the top N tags for the given interval:

```
2015-02-15 20:13:49,077 1.1.0.RELEASE  INFO RxComputationThreadPool-3 sink.top-tags - {"id":"8df84f9b-40ee-23c3-7473-fa611c43a19d","timestamp":1424049229077,"topTags":{"SNL40":18,"NBAAllStarNYC":4,"SpringXD":4}}

```

Distributed trending tags calculator
====================================

This example demonstrates how to create a distributed trending tags calculator as a multi-module project.

The example consists of:

- counter modules that count occurences of a tag over a sliding time window, distributing the count workload;
- intermediate modules that create partial tag rankings, emitting them at regular intervals;
- a final module that aggregates the partial rankings into a single final ranking, emitted at regular intervals.

## Requirements

In order to install the module run it in your Spring XD installation, you will need to have installed:

* Spring XD version 1.1.x ([Instructions](http://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started)). You'll need to build Spring XD with Java 8+ to use this sample (which uses lambda expressions).

## Building

mvn clean package

## Installing

Once Spring XD is started, run the following commands from the shell.

````
module upload --file [path-to]/spring-xd-samples/rxjava-top-tags-distributed/rxjava-top-tags-counter/target/rxjava-top-tags-counter-1.0.0-SNAPSHOT.jar --type processor --name top-tags-counter

module upload --file [path-to]/spring-xd-samples/rxjava-top-tags-distributed/rxjava-top-tags-intermediate-ranker/target/rxjava-top-tags-intermediate-ranker-1.0.0-SNAPSHOT.jar --type processor --name top-tags-intermediate-ranker

module upload --file [path-to]/spring-xd-samples/rxjava-top-tags-distributed/rxjava-top-tags-final-ranker/target/rxjava-top-tags-final-ranker-1.0.0-SNAPSHOT.jar --type processor --name top-tags-final-ranker

````

Create the stream

````
stream create twittertags --definition "twitterstream | transform --expression=#jsonPath(payload,'$.entities.hashtags[*].text') | splitter | top-tags-counter | top-tags-intermediate-ranker | top-tags-final-ranker | log" --deploy
````

Deploy the stream

````
stream deploy twittertags
````



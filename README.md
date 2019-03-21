Spring XD Samples
=================

This repository provides sample starter applications and code for use with the [Spring XD][] project. The following samples are available:

### analytics-dashboard

Standalone project which shows how you can use the [D3 Javascript library][] to create visualizations for Spring XD counters.

### analytics-pmml

Demonstrates the use of the analytics-pmml module by sending JSON data describing a Iris flower to an http source and using a PMML model to predict the species of Iris.

### batch-basic

Shows how to deploy a simple [Spring Batch][] process in [Spring XD][], without having to compile any code or install any jars.

### batch-notifications

This sample will take an input file containing payment data, and import the data into a database using [Spring XD][]'s batch job support. During import, various notification events are triggered and printed to the console.

### batch-hashtag-count

This sample will take an input file with Twitter JSON data and count the occurrences of hashtags.

### batch-simple

This is the quintessential *Hello World* example for [Spring XD][]. It consists of only one [Spring Batch] *Tasklet* which prints out `Hello Spring XD!`.

### batch-wordcount

This is the [Spring Batch word-count sample for Hadoop][] adapted for [Spring XD][]. This sample will take an input file and count the occurrences of each word within that document.

### groovy-script-sink

A custom module that illustrates how to bind variables defined as module options to a groovy script.

### hdfs-partitioning

Demonstrates the hdfs sink and its partitioning features. 

### kafka-message-bus

This sample walks you through setting up [Kafka][] as a message bus for [Spring XD][], also demonstrating partitining and failover.

### kafka-source

This sample walks you through setting up a [Kafka][] source in [Spring XD][], also demonstrating the use of multiple modules for ingestion efficiency.

### payload-conversion

This sample project implements a *custom processor* and demonstrates the use of the Tuple data type.

### pivotal-hd-demo

Demo using [Spring XD][] with [Pivotal HD][].

### reactor-moving-average

A custom module that uses Reactor's Stream API to perform stream analysis.

### reactor-tcp

Demonstrates the use of Reactor's TCP input source

### redis-store-sink

A simple custom sink module project implementing a Redis store, including integration tests.

### rss-feed-source

A simple custom source module project implementing an RSS feed, including integration tests.

### rxjava-moving-average

A custom module that uses RxJava's Observable API to perform stream analysis.

### si-dsl-module

This sample demonstrates how to create a custom module project, including integration tests, and packaging the module as an uber-jar using maven (requires Spring XD 1.1.x). The project also demonstrates the use of the [Spring Integration Java DSL][]

### smartgrid-prediction

This sample showcases a fictional use of Spring XD in the context of smart electricity metering and production. It was inspired by the https://www.cse.iitb.ac.in/debs2014/?page_id=42[ACM Distributed Event Based Systems Grand Challenge 2014].

### syslog

This sample demonstrates how to setup syslog ingestion from multiple hosts into HDFS.

### tweet-transformer-processor

A simple custom processor module project, including integration tests. This one has no external dependencies other than the processor implementation and Spring configuration which is provided either as XML or @Configuration.



[Spring XD]: https://github.com/spring-projects/spring-xd
[Spring Batch]: https://projects.spring.io/spring-batch/
[Spring Batch word-count sample for Hadoop]: https://github.com/SpringSource/spring-data-book/tree/master/hadoop/batch-wordcount
[D3 Javascript library]: https://d3js.org/
[Pivotal HD]: https://pivotal.io/products/pivotal-hd
[Kafka]: https://kafka.apache.org
[Spring Integration Java DSL]: https://github.com/spring-projects/spring-integration-java-dsl

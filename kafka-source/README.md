Kafka Source Demo
=================

In this example, you will learn how to set up an [Apache Kafka](https://kafka.apache.org) source for Spring XD. 

We will begin by demonstrating a very simple configuration based on the single node mode, and we will follow up with a
more elaborate one, demonstrating distribution and failover.

Prerequisites
-------------

In order to get started, make sure that you have the following components installed:

* Spring XD ([instructions](https://github.com/spring-projects/spring-xd/wiki/Getting-Started))
* Kafka - including ZooKeeper ([instructions](https://kafka.apache.org/documentation.html#quickstart))


Single-node mode
----------------

While Spring XD applications can take full advantage of the Apache Kafka message bus while working in distributed mode,
 Spring XD's singlenode mode is a quick and easy way to get started.

Start Zookeeper 3.4.6.  There is an installation script for this version in the spring xd distribution or download your
own copy.  In the Zookeeper installation directory start the server:

```
$ ./bin/zkServer.sh  start-foreground

Start the Kafka broker, for example in the Kafka installation directory,

```
./bin/kafka-server-start.sh config/server.properties
```

Now create a topic in Kafka. From the Kafka installation directory, run:

	$ ./bin/kafka-topics.sh --topic kafka-source-test --create --zookeeper localhost:2181 --partitions 6 --replication 1
	
Please note that we create a topic with 6 partitions (you can use any number for testing, but we have chosen 6 for this
example).

Start Spring XD in singlenode mode in the XD installation directory

```
$ cd $XD_HOME
$ ./bin/xd-singlenode
```

Note that there are two Zookeeper instances running, one standlone when starting the Kafka broker and another that is
embedded inside `xd-singlenode`.  You can configure the port number that `xd-singlenode` will use for its embedded Zookeeper instance to match what your Kafka broker is expecting to use.   By default the Kafka zookeeper host and port are configured to `localhost:2181`.  To set the port number that `xd-singlenode` will use, set the environment variable `ZK_EMBEDDED_SERVER_PORT=2181`.  Since we will be switching to XD distributed mode later in this example, it is better to use seperate Zookeeper server instances.

Start Spring XD shell

```
$ cd $XD_HOME/../shell
$ ./bin/xd-shell
```

In the XD Shell, deploy a simple stream with a kafka source (write the entire command on a single line):

	xd> stream create kafka-source-test --definition "kafka	--zkconnect=localhost:2181 --topic=kafka-source-test | log" --deploy
	
Send a few messages using the Apache Kafka console producer. From the Apache Kafka installation directory, run:

	$ ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic kafka-source-test 

Once the command is running, start typing at the console.
	
You should see the messages in the log, indicating that the Kafka source receives messages. 

This has created a single-threaded consumer for the topic. Should you wish to increase concurrency in the consumer,
you can use the `--streams` parameter as follows:

First, destroy the stream if necessary:

	xd> stream destroy kafka-source-test 

Then, deploy the stream with a higher concurrency setting for the Apache Kafka source.

	xd> stream create kafka-source-test --definition "kafka --zkconnect=localhost:2181 --topic=kafka-source-test --streams 3 | log" --deploy


Distributed mode
----------------

Simply increasing the concurrency in a single Kafka source is one way of improving performance, but does not use the
capabilities of Apache Kafka to their full extent. Running concurrent clients on the same machine (with the same NIC)
will rapidly saturate the receiving capabilities of the client.

A better way to improve the ingestion rate from Kafka is to create multiple Kafka sources in Spring XD, each running on
a separate container, and each consuming a subset of the overall partition set of the inbound topic. Also, this strategy
 improves fault tolerance.

(Please note that for the sake of simplicity, this demo will use containers deployed on the same machine. Please refer
to the Spring XD guide for details on how to run your containers on separate machines).

Start Apache ZooKeeper and Apache Kafka with default settings as described in the singlenode section.

Start Spring XD in distributed mode.  Check the instructions for starting Spring XD in distributed mode
[here](https://github.com/spring-projects/spring-xd/wiki/Running-Distributed-Mode)), and make sure that you started the
 following, as well:

	* HSQLDB
	* Redis

Start the admin node:

	$ xd-admin 
	
Start *three* containers, in separate terminals:

	$ xd-container
	
Start the shell:

	$ xd-shell

From the shell, create a sample stream again (destroy any existing stream if it exists):

	xd> stream create kafka-source-test --definition "kafka --zkconnect=localhost:2181 --topic=kafka-source-test | log --expression=payload.concat(' on partition  ').concat(headers['kafka__partitionId'].toString())" --deploy"
	
In what comes next, we will instruct Spring XD to deploy the modules (both Kafka source and log) on multiple containers.
This means that each available container will have a Kafka and a log module, and that they will be connected through direct channels, as described [here](https://github.com/spring-projects/spring-xd/wiki/Deployment#direct-binding). Execute the following command:

	xd> stream deploy kafka-source-test --properties "module.*.count=3"

For our demo's purpose, this means that each log sink will output messages displayed by the Kafka source that runs in the same container. This way, we can monitor the partitions that each source listens to. 

For the last step, we will compile and run the demo class that is attached to this project. It will send 1000 messages to the topic. The producer uses a simple Partitioner implementation that ensures that messages are evenly distributed across the partitions.

	$ ./gradlew run
	
Now, monitor the results in the container logs. You should see each container logging messages from a number of
partitions (typically, 2 each), e.g.

2015-02-10 13:20:23,600 1.1.0.SNAP  INFO dispatcher-1 sink.kafka-source-test - Message 96 at Tue Feb 10 13:20:23 EST 2015 on partition 0
2015-02-10 13:20:23,601 1.1.0.SNAP  INFO dispatcher-1 sink.kafka-source-test - Message 97 at Tue Feb 10 13:20:23 EST 2015 on partition 1

Note that messages logged by this example have a structure of `Message #messageNumber at Fri Nov 07 03:27:46 EST 2014 on partition #partitionNumber`.
This should help you identify the partitions that each source is listening to.

Now, shut down one container, and send messages again. You should see the remaining containers listening to their original partitions.

Next, restart the container, and keep sending messages. You should see the the newly arrived container receiving messages again,
resuming from where it left.

Now, start a fourth container and keep sending messages. You should see the existing containers receiving messages on their original partitions.

Shut down one of the previous containers. You should see the new container picking up reception from the one that just left.

For a more interesting test, you can try running the message sending code in an infinite loop, and try starting an stopping containers. 

Conclusions
-----------

In this sample, you have learned:

* How to set up a Kafka source in Spring XD.
* How to increase set up the number of streams that the source uses for consuming.
* How to increase the efficiency of data ingestion from Kafka by deploying multiple sources in a stream, using the distributed mode of Spring XD.
* How to failover works when multiple sources are set up.












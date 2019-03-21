Kafka Message Bus
=================

In this example, you will learn how to use [Apache Kafka](https://kafka.apache.org) as an inter-container transport for
Spring XD.

We will begin by demonstrating a very simple configuration based on the single-node mode, and we will follow with a
more elaborate one, showcasing distribution and failover.

Prerequisites
-------------

In order to get started, make sure that you have the following components installed:

* Spring XD 1.1.0.RELEASE or higher ([instructions](https://github.com/spring-projects/spring-xd/wiki/Getting-Started))
* Kafka 0.8.1.1 - including ZooKeeper ([instructions](https://kafka.apache.org/documentation.html#quickstart))


Single-node mode
----------------

While Spring XD applications can take full advantage of the Kafka message bus in distributed mode, the standalone mode
 is a quick and easy way to get started.

To begin, make sure that Spring XD is installed correctly.

Start Zookeeper and Kafka using default settings.

Start Spring XD in standalone mode using Apache Kafka as transport.
	
	$ xd-standalone --transport kafka

Start the Spring XD shell.

	$ xd-shell

In the shell, deploy a simple stream.

	xd> stream create httptest --definition "http --port=9999  | log" --deploy
    
Once the stream is deployed, a Kafka topic with the name `httptest.0`  is created for the stream. Partitioning is set
to one (as the singlenode application has a single container).

Next, monitor the state of the topics. From the Apache Kafka installation directory, run:

	$ bin/kafka-topics.sh --zookeeper localhost:2181 --describe
	
The expected result should look as follows

		Topic:httptest.0	PartitionCount:1	ReplicationFactor:1 Configs:
				Topic: httptest.0	Partition: 0	Leader: 0	Replicas: 0	Isr: 0 


In order to test that the message bus works, send some data from the shell to the http endpoint and watch for the
output in the logs.

	xd>http post localhost:9999 -d "1"
	
	xd>http post localhost:9999 -d "2"
	
	xd>http post localhost:9999 -d "3"

You should see the output in the console, which indicates that the http and log endpoints communicate successfully
through the Kafka message bus.

Distributed mode
----------------

In order to fully demonstrate the capabilities of using Kafka as a transport, we will start Spring XD in
distributed mode.

### Default stream partitioning

Start ZooKeeper and Apache with default settings (if not started already).

Check the instructions for starting Spring XD in distributed mode [here](https://github.com/spring-projects/spring-xd/wiki/Running-Distributed-Mode)), and make sure that you started the following:
	* HSQLDB
	* Redis

Start the admin node:

	$ xd-admin 

Set the environment property `XD_TRANSPORT` to `kafka` and start two containers, in separate shells. On a UNIX system, a simple command for doing so is:

	$ XD_TRANSPORT=kafka ; xd-container
	
Execute the above command twice, in separate terminals, to start two separate containers.

Start the shell:

	$ xd-shell

From the shell, create another stream:

	xd> stream create httptest-dist --definition "http --port=9998  | log"


For deploying the stream, you will use two settings that will illustrate how Kafka topic partitioning works with Spring XD. To do so, you will deploy 2 instances of the logging module. As a result, Spring XD will create a Kafka topic with two partitions, one for each module. Execute the following command in the Spring XD shell (make sure that it is all copied on one line):
  
	xd> stream deploy httptest-dist --properties "module.log.count=2"
	
Next, you will confirm that the topic has been created. From the Kafka installation directory, run:

	$ bin/kafka-topics.sh --zookeeper localhost:2181 --describe

The output should contain:
	
	Topic:httptest-dist.0	PartitionCount:2	ReplicationFactor:1	Configs:
		Topic: httptest-dist.0	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
		Topic: httptest-dist.0	Partition: 1	Leader: 0	Replicas: 0	Isr: 0

Note the topic created for the stream, and its two partitions. Each Spring XD container will listen to one of these
partitions.
	
Again, you will confirm that the message bus is functional, by sending a few messages and monitoring the log:

	xd>http post localhost:9999 -d "1"
		
	xd>http post localhost:9999 -d "2"
	
	xd>http post localhost:9999 -d "3"
	
You should see the output in the containers.

### Controlled stream partitioning and failover

By default, when the HTTP endpoint sends a message to the bus, and therefore to the associated Apache Kafka topic, a
partition is chosen randomly. This is a useful default, that ensures that the messages are balanced evenly between consumers.

Sometimes, there are business reasons for controlling stream partitioning in Spring XD, and the complete functionality
is described [here](https://github.com/spring-projects/spring-xd/wiki/Deployment#stream-partitioning).

In this case case, we would like to showcase the fact that the messages sent by the HTTP endpoint are indeed sent
to different partitions of the topic, and that each container receives messages sent to a particular partition.
In order to do so, undeploy the previous example first (if necessary):

	xd> stream undeploy httptest-dist

Subsequently, redeploy it with an additional property, whcih instructs Spring XD to pick up one partition or another
based on the message payload.

	xd> stream deploy httptest-dist \ 
	     --properties "module.log.count=2,module.http.producer.partitionKeyExpression=payload"
	
As a result of the previous command, an expression of the form `payload.hashCode()%2` will be evaluated for each
message, choosing either partition 0 or partition 1 as destination. (Spring XD will use the modulus of 2, because there are 2 partitions.)

Now start sending messages - as a side effect of our partitioning strategy, odd numbers are sent to one container,
even numbers to the other (because the hashcode of the numeric content is the value).

	xd> http post http://localhost:9999 --data "1"

	xd> http post http://localhost:9999 --data "2"
	
	xd> http post http://localhost:9999 --data "3"

Next, shut down one of the containers, and continue sending data.
	
	xd> http post http://localhost:9999 --data "4"
	
	xd> http post http://localhost:9999 --data "5"
	
	xd> http post http://localhost:9999 --data "6"
	
Note that remaining container will receive the messages that belong to it, thus proving that the message processing
continues for the stream (albeit in a single container).

Finally, restart the previous container.

	xd> http post http://localhost:9999 --data "7"
	
	xd> http post http://localhost:9999 --data "8"
	
	xd> http post http://localhost:9999 --data "9"
	
After the second container has been restarted, each container will receive messages from a single partition again,
restoring the distributed topology that you started initially.

Conclusion
----------

In this demo, you have learned:

* how to set up Kafka as a message bus for Spring XD;
* how Spring XD manages topic partitioning when creating a Kafka-based message bus;
* how Spring XD handles partition distribution across containers in a distributed scenario by allocating a number of
partitions at start;
* how in the case of container failure, in-flight messages are redirected to remaining working containers;
* how the addition (or restart) of a container causes a redistribution of the partitions across containers.







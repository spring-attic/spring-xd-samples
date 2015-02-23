# Product Analytics Example


## Overview

This is a Spring XD implementation of a [Storm Product Analytics Example](https://github.com/storm-book/examples-ch06-real-life-app). This example is documented in [Getting Started with Storm](http://ifeve.com/wp-content/uploads/2014/03/Getting-Started-With-Storm-Jonathan-Leibiusky-Gabriel-E_1276.pdf). The example uses a node.js application to track user product page hits and uses Redis to store and retrieve results. This example works the Storm example web app and requires the web app, Redis Spring XD (single node), and the Spring XD shell to be running.

This project includes the following XD custom modules:

* Redis source (will be provided by XD in the near future)
* Product Category Enricher
* User History Processor
* Product Categories Counter

The latter 3 modules use a common Spring Data Redis configuration which is provided by 'redis-common'. This is currently not configurable and will connect to Redis at 'localhost:6379' (the default connection).

The redis source is configurable, but must use the default connection for this example.


## Running The Example

* Download the original [storm example](https://github.com/storm-book/examples-ch06-real-life-app)
* Follow the instructions to install node.js in *Appendix C* of [the book](http://ifeve.com/wp-content/uploads/2014/03/Getting-Started-With-Storm-Jonathan-Leibiusky-Gabriel-E_1276.pdf) if necessary.

* Start the Redis server on localhost:6379 (the default), if necessary

* Start the sample node app from the Storm example project location:

````
$node webapp\app.js
````
Note: do not run the Storm component from the example while this example is running, they both consume messages from the same Redis *navigation* queue.

The web app loads test data when it starts up and runs on [http://localhost:3000]().

* Build this project. From the root location:

````
$mvn package
````

* Start an XD container (single node is fine) and the XD Shell

* Upload the custom modules to XD using the XD shell:

````
xd:> module upload --file path-to]/storm-product-analytics-example/spring-xd-redis-source/target/redis-source-1.0.0.BUILD-SNAPSHOT.jar --type source --name redis
xd:> module upload --file [path-to]/storm-product-analytics-example/product-category-enricher/target/product-category-enricher-1.0.0.BUILD-SNAPSHOT.jar --type processor --name product-category-enricher
xd:> module upload --file [path-to]/storm-product-analytics-example/user-history-processor/target/user-history-processor-1.0.0.BUILD-SNAPSHOT.jar --type processor --name user-history-processor
xd:> module upload --file [path-to]/storm-product-analytics-example/product-categories-counter/target/product-categories-counter-1.0.0.BUILD-SNAPSHOT.jar --type processor --name product-categories-counter
````
* Create and Deploy a Stream
````
xd:>stream create product-analytics --definition "redis --queue=navigation --outputType=application/x-xd-tuple | product-category-enricher | user-history-processor | product-categories-counter | log" --deploy
````
*Point your browser to [http://localhost:3000]() and navigate to some product pages. You should see the computed metrics displayed in the XD console log and the product stats page in the web app should reflect your navigation as described in the original example. Like so:

````
2015-02-19 16:28:56,629 1.2.0.SNAP  INFO org.springframework.integration.redis.inbound.RedisQueueMessageDrivenEndpoint#0-redis:queue-inbound-channel-adapter1 sink.product-analytics - {"id":"5da64396-a340-03e3-98c8-461bf317eee3","timestamp":1424381336625,"count":2,"product":"17","category":"Players"}
2015-02-19 16:28:56,630 1.2.0.SNAP  INFO org.springframework.integration.redis.inbound.RedisQueueMessageDrivenEndpoint#0-redis:queue-inbound-channel-adapter1 sink.product-analytics - {"id":"b3b8b7d7-7960-d726-42aa-7937d3784d80","timestamp":1424381336626,"count":1,"product":"2","category":"TVs"}
2015-02-19 16:28:56,630 1.2.0.SNAP  INFO org.springframework.integration.redis.inbound.RedisQueueMessageDrivenEndpoint#0-redis:queue-inbound-channel-adapter1 sink.product-analytics - {"id":"04eb2cdb-6716-3309-dacd-74d070ca0504","timestamp":1424381336627,"count":1,"product":"17","category":"Mounts"}
````
## How it Works

The XD stream is a drop in replacement for the Storm Topology for the example described in Chapter 6 of [Getting Started with Storm](http://ifeve.com/wp-content/uploads/2014/03/Getting-Started-With-Storm-Jonathan-Leibiusky-Gabriel-E_1276.pdf). 

The web app posts user navigation events to a Redis queue named *navigation*. The redis source is configured to consume events from this queue, as is the *UserNavigationSpout* in the original example. The source is configured in the stream definition above:

````
redis --queue=navigation --outputType=application/x-xd-tuple
````
The incoming JSON strings are converted to a Spring XD Tuple as the downstream processors work with Tuple payloads. Entries in the navigation queue contain the user id, product id, and the page type and look like:

````
{"user":"60571253-8315-40ff-8142-f8d68f9d35f0","product":"15","type":"PRODUCT"}
```` 
The next step is to enrich this payload with the associated product category:

````
{"user":"60571253-8315-40ff-8142-f8d68f9d35f0","product":"15","type":"PRODUCT","category":"Covers"}
````
The *product-category-enricher* extracts the product id and looks up its category from the Redis store (this assumes Redis is populated with test data: see above).

Next the *user-history-processor* updates the user history only if the current user has not visited this page before. This associates the newly visited product with all the categories of previously visited products by the current user. For a more detailed explanation, read Chapter 6 of Getting Started With Storm.  

The *product-categories-counter* aggregates the user history, totaling category counts. For example if a user navigates to a *Phone* product page and has previously looked at 2 different *Players*, the phone's product stats reflect a count of 2 in the Players category. The user-history-processor may emit these as separate events, e.g.

````
phone-product-id,"Players", 1
phone-product-id,"Players", 2
````
The product-categories-counter conflates these two messages to reflect only the last count. This module also splits the results into separate messages which we see in the container log. 

NOTE: The original Storm example notifies the webapp by posting these messages to http://localhost:3000/news which currently just writes them to the webapp console log. This can be done in XD using the OOTB *http-client*  processor, but is not implemented in this example.



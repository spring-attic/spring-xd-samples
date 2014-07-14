analytics-dashboard
===================

Standalone project which shows how you can use the D3 Javascript library to create visualizations for Spring XD counters.

<img src="dashboard.png" style="width:400px;"/>

Running the App
===============

To provide some data for appliation, start the XD server and then execute the following commands in the XD shell. 

NOTE: We're using `twitterstream` as a source which assumes the twitterstream module configuration  `$XD_MODULE_CONFIG_LOCATION/source/twitterstream/twitterstream.properties` is correctly populated with valid keys.

    xd:> stream create tweets --definition "twitterstream | log"

    xd:> stream create tweetlang  --definition "tap:stream:tweets > field-value-counter --fieldName=lang" --deploy

    xd:> stream create tweetcount --definition "tap:stream:tweets > aggregate-counter" --deploy

    xd:> stream create tagcount --definition "tap:stream:tweets > field-value-counter --fieldName=entities.hashtags.text --name=hashtags" --deploy

    xd:> stream deploy tweets

These taps correspond to recording

* A count of tweet's language.  The incoming JSON data from the twitter stream has this information located under the field named 'lang'.  This is done using the [Field Value Counter sink](https://github.com/spring-projects/spring-xd/wiki/Analytics#field-value-counter).  The name of the counter default to the name of the stream, 'tweetlang'
* Storing the number of tweets in time buckets of minute, hour, day, month and year.  This information is used to show the graph of tweets per minute in the UI and is based on the [Aggregate Counter](https://github.com/spring-projects/spring-xd/wiki/Analytics#aggregate-counter)
* A count of the hashtags in the tweets.  The incoming JSON data from the twitter stream has this information ocated under the path 'entities.hashtags.text'.  The name of the counter is explicitly specified to be 'hashtags'.


All that's required to run the app is to serve up the `public` directory from a web server listening on port 9889. There are various ways you can do this. For example, you can use the Spring Boot CLI. If you have it [installed](http://docs.spring.io/spring-boot/docs/1.1.4.RELEASE/reference/html/getting-started-installing-spring-boot.html#getting-started-installing-the-cli), run

    spring run dashboard.groovy

Or if you have ruby installed, run

    ruby -run -e httpd ./public -p 9889

from the `analytics-dashboard` directory. 

Alternatively, if you have nginx installed, 
    
    start-nginx.sh

Then point your browser at [http://localhost:9889/dashboard.html](http://localhost:9889/dashboard.html).

The page is a simple dashboard divided in half. The right hand side will show a field value counter as a bubble chart. The left is divided into two panels, one which will show a field value counter as a pie chart, and one below it for an aggregate counter bar chart. The menus are populated by querying the Spring XD admin server, which is expected to also be running on localhost. You can toggle between the different field-value counters, the one for hashtags is best displayed using a bubble chart.

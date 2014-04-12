Spring XD and GemFire Real Time Analytics Example
=

Description
---
The _gemfire-analytics_ project illustrates a simple use case for using [GemFire](http://www.gopivotal.com/pivotal-products/data/pivotal-gemfire) with [Spring XD](http://projects.spring.io/spring-xd/) for real time analytics. It taps a twitter feed or twitter search and loads summary data along with the hash tags into GemFire for every tweet in the feed. Cached entries are set to expire after 15 minutes in order to conserve memory, and this is considered an appropriate window for real time analytics.

The [HashTagAnalyzerFunction](https://github.com/dturanski/SpringOne2013/blob/master/gemfire-demo/hashtag-analyzer/src/main/java/org/springframework/xd/demo/gemfire/function/HashTagAnalyzerFunction.java) is an example of a complex aggregation algorithm for which a GemFire [function execution](http://pubs.vmware.com/vfabric53/topic/com.vmware.vfabric.gemfire.7.0/developing/function_exec/chapter_overview.html) is appropriate. The function accepts a target hash tag, e.g. "java" and iterates over all cached entries returning an aggregate count of associated hash tags. That is, it counts the total occurences of additional hashtags appearing in tweets that contain the target hash tag. For example: 

	new HashTagAnalyzerFunction().aggregateAssociatedHashTags(data "java") 

yields a result like the following: So tweets containing the #java hashtag most frequently are about jobs. 12 also contained #appengine … interesting…
	
	jobs:12
	appengine:12
	job:11
	php:4
	soudev:4
	sql:4
	js:4
	html5:3
	jobboard:3
	css:3
	opdrachten:3
	ios:3
	desarrolladores:3
	braziljs:3
	javascript:3
	j2se:2
	desktop:2
	programadores:2
	hibernate:2
	asp:2
	development:2
	computers:2
	programming:2
	c:2
	framework:2
	developer:2
	contratando:2
	jetty:2
	…
	
Running this as a GemFire function means this code is co-located with the data and may be executed as a single call to GemFire. This is much more efficient than pulling all the cached entries over the network to a remote process in order to perform the calculation. The performance improvement is sigificant for large data sets. Additionally, using a [partitioned region](http://pubs.vmware.com/vfabric53/topic/com.vmware.vfabric.gemfire.7.0/developing/partitioned_regions/how_partitioning_works.html), this work may be distributed among cache members and run in parallel; the final results from each member aggregated in the node that invoked the function execution. This is similar to the way map-reduce works. 


Set Up
----

Running the example requires

* Download and unzip the [Spring XD distribution](http://repo.spring.io/simple/libs-milestone-local/org/springframework/xd/spring-xd/1.0.0.M4/spring-xd-1.0.0.M4-dist.zip), if you haven't done so already

* Build the gemfire-demo project and install required artifacts to XD

		$ cd gemfire-demo
		$ ./gradlew jar

* Edit the install script to point XD_HOME to the location of your XD installation then run the script to copy custom artifacts needed for the example :

		$ ./install

* Start the Gemfire server included with the XD distribution using the configuration provided for this example.

     	$ cd $XD_HOME/gemfire
     	$ bin/gemfire-server config/twitter-demo.xml

* Start the XD SingleNode server

		$ cd $XD_HOME/xd
		$ bin/xd-singlenode 
		
* Start the XD Shell
		
		$cd $XD_HOME/shell
		$bin/xd-shell

* Create a feed
	* Create a mock twitter feed

			$xd> stream create tweets --definition "tail --name=$FILE --fromEnd=false | randomDelay --max=200 | log"
			
     where _$FILE_ is the absolute path of gemfire-analitics/data/javatweets.out in this case.

		
    *	Create a real twitter feed. Here we are using *twittersearch* to filter for tweets containing hashtags of interest. Feel free to experiment with the search string. This ensures we will capture tweets that actually have hashtags as the full twitter stream contains many tweets with no hashtags.  Note the inputType parameter on the log sink. The *twittersearch* source emits Spring Social Tweet objects by default. This is what we want for the tap. Setting *inputType=application/json on the log renders these objects as JSON to be human-readable, but the demo will work fine without this setting.
	
			xd:> stream create tweets --definition "twittersearch --query='#spring+OR+#java+OR+#groovy+OR+#grails+OR+#javascipt+OR+#s12gx' | log --inputType=application/json"
			
	NOTE: you can also try using *twitterstream* for this example, There is no guarantee that tweets will contain any hashtags but it should work. The *twitterstream* source emits native JSON from twitter instead of Tweet objects.

* Set up a gemfire tap on the tweets stream. The tap uses a a custom groovy script to transform the full Tweet object to a simple *TweetSummary* type, used to extract only the relavant information. The TweetSummary is cached using the unique twitter id as a cache key, given as a SpEL expression. 

		xd:>stream create hashtags --definition "tap:stream:tweets  > transform --script=tweetSummary.groovy | gemfire-server --keyExpression=payload['id']" --deploy
	
* Now that the tap is ready, start the feed

		xd:>stream deploy tweets 

NOTE: See the [XD Documentation](https://github.com/spring-projects/spring-xd/wiki/Sources#wiki-twittersearch) re. twitter authorization requirements

We have started the twitter feed and should be populating the cache. Entry create messages should be logged to the cache server console.  The _hashtags_ tap converts each  twitter payload to a )[TweetSummary](https://github.com/dturanski/SpringOne2013/blob/master/gemfire-demo/hashtag-analyzer/src/main/java/org/springframework/xd/demo/gemfire/TweetSummary.java) containing selected fields. The custom module, groovy scripts, and the jar containing the transformer and any other required classes were already installed to XD via the install script.

The hasshtag-ananlyzer.jar is also copied to GemFire's classpath since [HashTagAnalyzerFunction](https://github.com/dturanski/SpringOne2013/blob/master/gemfire-demo/hashtag-analyzer/src/main/java/org/springframework/xd/demo/gemfire/function/HashTagAnalyzerFunction.java) is configured as a GemFire remote function and will run in the cache server process when invoked. Additionally, the function references _TweetSummary_ so that must be on GemFire's classpath as well.
	
* Start the hashtag REST service by running [Application](https://github.com/dturanski/SpringOne2013/blob/master/gemfire-demo/hashtag-rest/src/main/java/org/springframework/xd/demo/gemfire/Application.java) in the _hashtag-rest_ project:

	* Build an executable jar:
 		
 			$ ./gradlew build
 	* Run it
 			
 			$ java -jar hashtag-rest/build/libs/hashtag-rest.jar 	
 	* Alternately, you can run this application in Eclipse or Idea. (NOTE: I had to fix the classpath by setting the run configuration classpath to only include exported entries. Then select the following exported jars from hashtag-analyzer properties: spring-data-gemfire, gemfire, spring-data-commons, spring-tx )
 
 			$ ./gradlew eclipse
	    

The REST service was built with [Spring Boot](http://projects.spring.io/spring-boot/) and runs by default on localhost:8080. 

* Start the service and point your browser to [http://localhost:8080/hashtagcounts/java](http://localhost:8080/hashtagcounts) This will list all hashtags captured in the feed.

* Invoke the associated hashtags analysis. Open the browser to 
[http://localhost:8080/associatedhashtags/java](http://localhost:8080/associatedhashtags/java)  where _java_ is the value of a path variable containing the target hashtag. This will invoke the remote funciton and return the results as JSON (No eye candy implemented yet). You should see something like:

	{"jobs":12,"appengine":12,"job":11,"php":4,"soudev":4,"sql":4,"js":4,"html5":3,"jobboard":	3,"css":3,"opdrachten":3,"ios":3,"desarrolladores":3,"braziljs":3,"javascript":3,"j2se":	2,"desktop":2,"programadores":2,"hibernate":2,"asp":2,"development":2,"computers":	2,"programming":2,"c":2,"framework":2,"developer":2,"contratando":2,"jetty":2,"nuevoleￃﾳn":	2,"vacatures":2,"xml":2,"logic":2,"cs":2,"mￃﾩxico":2,"android":2,"engineering":	2,"windowsazure":	2,"monterrey":2,"game":2,"html":2,"shell":1,"feinabarcelona":1,"caffeine":	1,"j2ee":1,"cafￃﾩ":	1,"stringstextinputoutputi":1,"lambda":1,"brew":1,"batiktulis":1,"songket":	1,"servlets":1,"bag":	1,"like":1,"like4like":1,"startup":1,"7u40":1,"karawitan":	1,"gesformexico":1,"rijobs":1,"net":	1,"spring":1,"javajob":1,"crafts":1,"vacantes":	1,"telecommunications":1,"giftfromjogja":	1,"pictofme":1,"cupofjoe":1,"java8":1,"bali":	1,"consulting":1,"morningmud":1,"littforsinket":	1,"traditional":1,"web":1,"developers":1,"istimewa":1,"testing":1,"kansas":1,"ca":1,"struts":	1,"love":1,"python":1,"automotive":1,"software":1,"sofwaredeveloper":1,"javaee":1,"processing":	1,"fields":1,"organigrama":1,"espresso":1,"coffee":1,"class":1,"likeaboss":1,"picture":	1,"empleo":1,"rest":1,"reflection":1,"sublimetext":1,"ipad":1,"rh":1,"routine":1,"bcnjobs":	1,"vmware":1,"oracle":1,"scala":1,"programador":1,"network":1,"eclipse":1,"ejb":1,"creativeblog":	1,"cappuccino":1,"sumatra":1,"latte":1,"develop":1,"anny":1,"sketch":1,"engineer":1,"instrument":	1,"iphone":1,"database":1,"employeeit":1,"estructurastablasconsusrelaciones":1,"group":	1,"engineers":1,"energy":1}
	
The REST API also includes 

[http://localhost:8080/watchhashtag/{target}](http://localhost:8080/watchhashtag/java) which 
illustrates the use of GemFire's Continuous Query capability. This is implemented for long polling using Spring MVC's asynchronous support. The initial invocation for a new target hash tag will return the the current result set and create a continuous query so subsequent invocations will return any new tweets matching the target.

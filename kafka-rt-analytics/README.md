#Spring XD Kafka RT Analysis

##Contents

* **firehose-simulator** - Generates events. The total number of sources emiting events and the distribution of sources and events are configuruble. Output payload type : `AppEvent`. See [firehose-simulator/README.md](firehose-simulator/README.md)
*  **event-aggregator** - Aggregates events (raw data) by source into 1 second buckets. Input payload type: `AppEvent`. Output payload type: `SourceEventBucket`
* **metrics** - Calculates various metrics for each bucket. Output payload type: `List<Tuple>`. Currently configured with metrics: `eventCount`, `httpErrorCount`, `loginCount`, `httpResponseTime`,`errorMessageCount`, processed serially.
* **event-domain** - Common domain types

##Requirements

Requires Java 1.8

##Installing in XD

Edit scripts `copyLibs.sh` and `uploadModules.script` and change the paths for your environment.

Build:

```
$mvn clean package (-DskipTests)
```

Copy shared jars to xd/lib

```
$./copyLibs.sh
```

Start XD runtime

Install custom modules

```
xd:>script <path-to>/uploadModules.script
```


##Sample Streams

These streams currently process up to ~28k/sec with xd-singlenode my MacBook Pro and output individual metrics as Tuples.

```
stream create basic --definition "firehose-simulator | event-aggregator | metrics | splitter | log"
```

```
stream create eventCount --definition "firehose-simulator --numSources=10  --sourceDistribution=75,20 | event-aggregator | metrics | splitter | filter --expression='payload.getString(''metricName'')==''eventCount'''| log"
```

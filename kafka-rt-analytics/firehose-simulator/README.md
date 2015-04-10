# firehose-simulator
Spring XD source which emits random AppEvents given distribution profiles (percentiles histogram) for sources (app0 - app9999) and event types, currently (`httpRequest`, `login`, `logMessage`)in order to test the effects of data skew, or narrow the range of data values. Each event type includes randomly generated attributes, such as `statusCode` for `httpRequest` which is weighted toward 200, but a small percentage contains an error code (404, 403, 500). The response times are also randomly generated.  Likewise `logMessage` is weighted for `INFO` messages but also contain `ERROR`,`WARN`, and `DEBUG`.

##Module options

* **eventDistribution, sourceDistribution** - given as a comma-delimited list of int. For example an eventDistribution of 98,1,1 emits 98% the first event type, 1% the next, 1% the next, etc. The total <= 100. If less, the remaining values selected at random, evenly distributed.

Source distribution applies to sources named `app0` ... `appN` where N is determined by `numSources`

Event distribution applies to {public static enum EventType {httpRequest, login, logMessage} in this order. 
See [AppEvent.java](../event-domain/src/main/java/org/springframework/xd/samples/rt/event/AppEvent.java#L30).

* **numSources** - The default is 10000, specifying the range of unique event sources.

## Changing distribution on the fly

The module is configured with a background task that loads `eventDistribution` and `sourceDistribution` from a properties resource `classpath:firehose.properties`. This location (e.g., `modules/processor/scripts/firehose.properties` or `xd/config/firehose.properties`) is checked every 5 seconds and will update the firehose distribution. To reset to a flat distribution, use, e.g., 

```
eventDistribution=
```
This currently does not affect numSources.






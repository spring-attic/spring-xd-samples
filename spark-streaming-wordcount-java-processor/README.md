Spring XD spark streaming processor example
=================

### Setup and configure spark cluster

Spark streaming module can be run on any of the supporting cluster modes.
You can either setup spark `standalone` cluster or use `mesos`, `yarn`. For instructions, see [spark cluster overview] (https://spark.apache.org/docs/1.2.1/cluster-overview.html) and [spark standalone setup] (https://spark.apache.org/docs/1.2.1/spark-standalone.html).

For the development experience, you can use `local` mode as well.

Once the spark cluster is setup, you can configure the property `spark.master` in XD servers.yml to use the corresponding cluster manager URL.The `spark.master` URL property can be configured at the module implementation using `@SparkConfig` annotation that returns Spark configuration properties:

```
  @SparkConfig
	public Properties getSparkConfigProperties() {
		Properties props = new Properties();
		// Any specific Spark configuration properties would go here.
		// These properties always get the highest precedence
		props.setProperty(SPARK_MASTER_URL_PROP, "local[4]");
		return props;
	}
```

By default, Spring XD is set to use `spark://localhost:7077`. Please note that if running Spring XD in standalone mode, it is required to run the Spark cluster in local mode, and therefore to set the `spark.master` property to `local[<n>]`, where `n` is a greater than 1 integer representing the worker threads of the cluster (for the purpose of this example `local[2]` should be sufficient).

### Upload the module

Spring XD provides uploading modules archive from the `shell` interface. We can use this approach to upload the word count example module into XD's module registry.

1. Run the build to generate the jar

  ```
    ./gradlew clean build
  ```
  This will generate the spark-streaming-wordcount-java-processor-0.1.0.jar under build/libs.
  
2. Upload the generated jar into XD module registry

  ```
    module upload --file [path to]/spark-streaming-wordcount-java-processor/build/libs/spark-streaming-wordcount-java-processor-0.1.0.jar --name java-word-count --type processor
  ```
  
### Deploy the stream
1. Once the module is uploaded, we can start creating the stream

  ```
  stream create spark-streaming-word-count --definition "http | java-word-count | log" --deploy
  ```
  
  Note: To add a tap at the `output` of Spark streaming processor module, the option `--enableTap` should be set
  In that case, the stream definition would look like this:
  
  ```
  stream create spark-streaming-word-count --definition "http | java-word-count --enableTap=true | log" --deploy
  ```
  
2. To add a tap at the `output` of Spark streaming processor module, 

  ```
  stream create tap-word-count --definition "tap:stream:spark-streaming-word-count.java-word-count > counter --name=word-counter" --deploy
  ```
  
3. Post messages to the stream

  ```
    http post --data "foo foo foo"
  ```
  
4. The log sink module would show the word count results at the container log. This shows the word count computation happens at the spark cluster by the `java-word-count` module and the result is sent to the log module.

  ```
  .....  INFO xdbus.a1.1-1 sink.a1 - (foo,3)
  ```

5. For each message output at the Spark streaming word-count module, the tap stream above will count.

   ```
   counter display word-counter
   ```
   
The above command will show the counter value.

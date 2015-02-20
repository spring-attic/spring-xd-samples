Spring XD spark streaming java based sink example
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

By default it is set to use `spark://localhost:7077`

### Upload the module

Spring XD provides uploading modules archive from the `shell` interface. We can use this approach to upload the logger example module into XD's module registry.

1. Run the build to generate the jar

  ```
    ./gradlew clean build
  ```
  This will generate the spark-streaming-logger-java-sink-0.1.0.jar under build/libs.
  or, you can skip this step and copy the upload-ready jar located at `.upload-ready-jar/`
  
2. Upload the generated jar into XD module registry

  ```
    module upload --file <location of the jar file>/spark-streaming-logger-java-sink-0.1.0.jar 
    --name java-logger --type sink 
  ```
  
### Deploy the stream
1. Once the module is uploaded, we can start creating the stream

  ```
  stream create spark-streaming-java-logger --definition "http | java-logger | log" --deploy
  ```
  
2. Post messages to the stream

  ```
    http post --message "foo foo foo"
  ```
3. The data "foo foo foo" is expected to be available at java-logger module's application's `stdout` in the spark cluster

  ```
   foo foo foo
  ```


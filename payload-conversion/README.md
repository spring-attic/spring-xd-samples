Payload Conversion
==================

This sample project implements a custom processor module named *myTupleProcessor*. When deployed to a Spring XD container, the sample demonstrates the use of the [Tuple][] data type. The module is backed by a java class *MyTupleProcessor* that currently returns the Tuple passed as an argument to its *process(Tuple tuple)* method. This code may be customized to do more interesting things as long as it returns a Tuple as a result.

This sample also illustrates XD's built in payload conversion. This module may be linked to a source that produces any JSON string.

### Running the example

Build the project by executing:

	$ mvn clean assembly:assembly

This will result in the following files under `target/payload-conversion-1.0.0.BUILD-SNAPSHOT-bin/`:

```
└── modules
    └── processor
        └── myTupleProcessor
            ├── config
            │   └── myTupleProcessor.xml
            └── lib
                └── payload-conversion-1.0.0.BUILD-SNAPSHOT.jar
```

Install the module to an XD installation:

	$ cp -R target/payload-conversion-1.0.0.BUILD-SNAPSHOT-bin/* $XD_HOME/modules

Next, start the XD container and the XD admin process, either single-node, or distributed. And start the XD shell. Create and deploy a stream:

	xd:>stream create test --definition "http | myTupleProcessor --inputType=application/x-xd-tuple | file" --deploy

Post some JSON content to the stream's http source:

	xd:>http post --target http://localhost:9000 --data {"symbol":"FAKE","price":75} --contentType "application/json"

You should see the Tuple rendered as JSON:
	
	xd:>cat /tmp/xd/output/test.out
	
	{"id":"719f5276-22d2-434d-87dc-39a23a978077","timestamp":1376387174881,"symbol":"FAKE","price":"75"}

### What's Happening Under Hood

XD stream definitions support module parameters *inputType* and *outputType* and support certain type conversions out of the box. XD associates the content type *application/x-xd-tuple* with the Java type *org.springframework.xd.tuple.DefaultTuple* and can convert any valid JSON string to a tuple as requested. More details on XD type conversion can be found [here](https://github.com/spring-projects/spring-xd/wiki/Type-Conversion).
    
The conversion specified by the *inputType* parameter causes the JSON content to be converted to a Tuple which is what the MyTupleProcessor.process() method requires. The result of that method is piped to a file sink which renders the Tuple invoking its toString() method, which also happens to be JSON. Note the *id* and *timestamp* properties, indicating that the Tuple conversion was performed.   

[Tuple]: https://github.com/spring-projects/spring-xd/wiki/Tuples
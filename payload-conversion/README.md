Payload Conversion
==

This sample project implements a custom processor module named *myTupleProcessor*. When deployed to an XD container, demonstrates the use of the Tuple data type. The module is backed by a java class *MyTupleProcessor* that currently returns the Tuple passed as an argument to its *process(Tuple tuple)* method. This code may be customized to do more interesting things as long as it returns a Tuple as a result. 

This sample also illustrates XD's built in payload conversion. This module may be linked to a source that produces any JSON string.

Running the example
---
To run this sample, deploy the module to an XD installation:

     $cp modules/processor/myTupleProcessor.xml $XD_HOME/modules/processor
     $./gradlew jar
     $cp build/libs/payload-conversion.jar $XD_HOME/xd/lib
  
Next, start the XD container and the XD admin process, either single-node, or distributed. And start the XD shell. Create a stream

     xd>create stream test "http | myTupleProcessor --inputType=application/x-xd-tuple | file"
     
Post some JSON content to the stream's http source
     
     xd>http post --target http://localhost:9000 --data "{"symbol":"VMW","price":75}"     
     
You should see the Tuple rendered as JSON:
	
	 >cat /tmp/xd/output/test.out
     
    {"id":"719f5276-22d2-434d-87dc-39a23a978077","timestamp":1376387174881,"symbol":"VMW","price":"75"}
    
What's Happening Under Hood
----
XD stream definitions support module parameters *inputType* and *outputType* and support certain type conversions out of the box. XD associates the content type *application/x-xd-tuple* with the Java type *org.springframework.xd.tuple.DefaultTuple* and can convert any valid JSON string to a tuple as requested. More details on XD type conversion can be found [here](https://github.com/spring-projects/spring-xd/wiki/Type-Conversion).
    
The conversion specified by the *inputType* parameter causes the JSON content to be converted to a Tuple which is what the MyTupleProcessor.process() method requires. The result of that method is piped to a file sink which renders the Tuple invoking its toString() method, which also happens to be JSON. Not the *id* and *timestamp* properties, indicating that the Tuple conversion was performed.   
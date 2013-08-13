Payload Conversion
==

This sample project implements a custom processor module named *myTupleProcessor*. When deployed to an XD container, demonstrates the use of the Tuple data type. The module is backed by a java class *MyTupleProcessor* that currently returns the Tuple passed as an argument to its *process(Tuple tuple)* method. This code may be customized to do more interesting things as long as it returns a Tuple as a result. 

This sample also illustrates XD's built in payload conversion. This module may be linked to a source that produces any JSON string. The output of the source must contain a *content-type* header indicating that the payload is *application/json*

Running the example
---
To run this sample, deploy the module to an XD installation:

     $cp modules/processor/myTupleProcessor.xml $XD_HOME/modules/processor
     $./gradlew jar
     $cp build/libs/payload-conversion.jar $XD_HOME/xd/lib
  
Next, start the XD container and the XD admin process, either single-node, or distributed. And start the XD shell. Create a stream

     xd>create stream test "http | myTupleProcessor | file"
     
In another terminal window, post JSON to the stream using curl (The XD shell does not currenly support HTTP headers)

     $ curl -H "content-type :application/json" -X POST -d "{'symbol':'VMW','price':75}" http://localhost:9000
     $cat /tmp/xd/output/test.out 
     
You should see the Tuple rendered as JSON:
     
    {"id":"719f5276-22d2-434d-87dc-39a23a978077","timestamp":1376387174881,"symbol":"VMW","price":"75"}
    
What's Happening Under Hood
----
The XD container recognizes the content-type application/json. Additionally, the *myTupleProcessor.xml* module declares *DefaultTuple* as its expected data type:

```
     <util:list id="accepted-content-types">
		<value>application/x-java-object;type=org.springframework.xd.tuple.DefaultTuple</value>
	</util:list>   
```
    
This cause XD to convert the JSON content to a Tuple which is what MyTupleProcessor.process() requires. The result of that method is piped to a file sink which renders the Tuple as JSON.   



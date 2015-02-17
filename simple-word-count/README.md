Simple Word Count Demo
======================

In this example you will learn how to create a simple word count application with Spring XD, 
and you will do that without writing a single line of code, relying on the native capabilities 
of Spring XD only. 

Prerequisites
-------------

In order to get started, make sure that Spring XD is installed. You can find the instructions 
([here](https://github.com/spring-projects/spring-xd/wiki/Getting-Started)).

Standalone mode
---------------

Start Spring XD in standalone mode.

First, we create a normal ingestion stream, with the following modules:

* a file source that monitors a directory containing text files - whenever a new file is copied there,
the source will read it, each of the messages that it produces containing one line;
* a splitter that receives the lines of text produced by the source, and splits them into words, using
space as a separator (you can change the SpEL expression for more accuracy);
* a sink that logs the emitted words.  

```
stream create words --definition "file --dir=<directory-with-text-files> --outputType=text/plain | splitter --expression=payload.split(' ')  | log
```

Then we will tap the original stream and create a new one, for counting the words. We will use a field value counter for accumulating results in the 
* the `words` stream is tapped after the splitter;
* we use a transformer to wrap the individual words into Spring XD `Tuple`s, which are one of the expected inputs for the field value counter.


```
stream create wordcount --definition "tap:stream:words.splitter > transform --expression=T(org.springframework.xd.tuple.TupleBuilder).tuple().of('word',payload) | field-value-counter --fieldName=word"
```

You can use the [analytics-dashboard]() project to visualize the word counts as the stream is processed.

Distributed mode
----------------

In a distributed mode, the scenario is identical, except the module counts can be adjusted.


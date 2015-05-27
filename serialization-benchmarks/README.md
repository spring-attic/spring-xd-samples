Serialization Benchmarks
=========================
This project includes serialization microbenchmarks using Spring XDs default `PojoCodec` implementation used internally by the MessageBus and built using [Kryo][]. Serialization performance depends primarily on the size and structure of the object to be serialized but may be optimized significantly as shown in these sample benchmarks. 

## Sample Benchmark Tests

The project includes tests that extend `AbstractCodecBenchMarkTest`. The base class implements the test fixture. Each concrete test must provide an object instance to be serialized (and deserialized) and a configured PojoCodec instances. After reviewing the sample code, implementing your own serialization benchmark should be straightforward. 

* _MediaContentCodecBenchmarkTest_ uses the same object populated with identical content (or very similar) as used for the serialization benchmarks in the [jvm-serializers][] project. This test is included to verify that the PojoCodec performance is comparable to an independent benchmark using native Kryo classes (the jvm-serializers `kryo-manual` benchmark).

In addition, three benchmark tests against customer Order domain objects  illustrate both the impact of the data type on performance and provides a comparison of optimization techniques:

* _BaselineOrderCodecBenchmarkTest_ uses default settings. No optimization is applied
* _SerializableOrderCodecBenchmarkTest_ uses an alternate implementation of the domain types which implement Kryo's `Serializable` interface. This requires no additional configuration in Spring XD beyond disabling Kryo's use of object [references][].
* _OptimizedOrderCodecBenchmarkTest_ uses custom serializers and class IDs registered to Kryo. This yields the fastest times but requires installing custom code along with some Spring configuration in the Spring XD runtime. 

Details of how to optimize serialization in Spring XD are included in the [Optimizing Serialization][] section of the Spring XD Reference Guide.  

[Kryo]: https://github.com/EsotericSoftware/kryo
[jvm-serializers]: https://github.com/eishay/jvm-serializers/wiki
[references]: https://github.com/EsotericSoftware/kryo#references[references]
[Optimizing Serialization]: http://docs.spring.io/spring-xd/docs/current-SNAPSHOT/reference/html/#optimizing-serialization

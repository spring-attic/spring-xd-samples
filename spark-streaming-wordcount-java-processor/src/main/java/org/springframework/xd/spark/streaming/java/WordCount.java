/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.spark.streaming.java;

import java.util.Arrays;
import java.util.Properties;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import org.springframework.xd.spark.streaming.SparkConfig;

import scala.Tuple2;

/**
 * @author Mark Fisher
 * @author Ilayaperumal Gopinathan
 * @since 1.1
 */
@SuppressWarnings({"serial"})
public class WordCount implements Processor<JavaDStream<String>, JavaPairDStream<String, Integer>> {

	@Override
	public JavaPairDStream<String, Integer> process(JavaDStream<String> input) {
		JavaDStream<String> words = input.flatMap(word -> Arrays.asList(word.split(" ")));
		JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<String, Integer>(s, 1))
				.reduceByKey((i1, i2) -> i1 + i2);
		return wordCounts;
	}

	@SparkConfig
	public Properties getSparkConfigProperties() {
		Properties props = new Properties();
		// Any specific Spark configuration properties would go here.
		// These properties always get the highest precedence
		//props.setProperty(SPARK_MASTER_URL_PROP, "local[4]");
		return props;
	}
}

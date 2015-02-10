/*
 * Copyright 2014 the original author or authors.
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


package org.springframework.xd.demo.kafka;

import java.util.Date;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * @author Marius Bogoevici
 */
public class KafkaSourceDemo {

	public static void main(String[] args) throws Exception {
		Properties producerConfigProperties = new Properties();
		producerConfigProperties.setProperty("metadata.broker.list", "localhost:9092");
		producerConfigProperties.put("partitioner.class", KafkaPartitioner.class.getName());

		ProducerConfig config = new ProducerConfig(producerConfigProperties);
		Producer<byte[], byte[]> producer = new Producer<byte[], byte[]>(config);
		for (int i = 0; i < 100; i++) {
			KeyedMessage<byte[], byte[]> message = new KeyedMessage<byte[], byte[]>("kafka-source-test", (i + "").getBytes(),
					("Message " + i + " at " + new Date()).getBytes());
			producer.send(message);
		}
	}
}
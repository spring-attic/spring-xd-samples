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

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
* @author Marius Bogoevici
*/
public class KafkaPartitioner implements Partitioner {

	public KafkaPartitioner(VerifiableProperties verifiableProperties) {

	}

	/**
	 * A basic partitioner, that takes the
	 * @param key
	 * @param numPartitions
	 * @return
	 */
	@Override
	public int partition(Object key, int numPartitions) {
		String stringAsByteArray = new String((byte[]) key);
		int i = new Integer(stringAsByteArray) % numPartitions;
		return i;
	}
}

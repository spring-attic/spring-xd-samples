/*
 * Copyright 2014 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.samples.serialization.orders;

import org.springframework.xd.dirt.integration.bus.serializer.kryo.PojoCodec;
import org.springframework.xd.samples.serialization.AbstractCodecBenchmarkTest;

/**
 * Sample Benchmark for Order domain using default settings.
 *
 * @author David Turanski
 */
public class BaselineOrderCodecBenchmarkTest extends AbstractCodecBenchmarkTest {
	@Override
	protected PojoCodec getPojoCodec() {
		return new PojoCodec();
	}

	@Override
	protected Object getObjectToSerialize() {
		// Return baseline instances (do not implement serializable)
		return OrderCreator.getInstance(false);
	}
}

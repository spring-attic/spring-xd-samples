/*
 * Copyright 2014 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.samples.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.io.Output;
import org.junit.Before;
import org.junit.Test;

import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.xd.dirt.integration.bus.serializer.kryo.PojoCodec;

/**
 * Base class for microbenchmarking PojoCodec.
 *
 * @author David Turanski
 */
public abstract class AbstractCodecBenchmarkTest {
	private StopWatch stopWatch;

//	protected static final int ITERATIONS = 50000;

	protected final static int ITERATIONS = 2000;

	protected final static int TEST_DURATION_MILLIS = 15000; // 15 seconds

	private final static Map<String, Integer> counts = new HashMap<String, Integer>();

	@Before
	public void setUp() throws IOException {
		stopWatch = new StopWatch("PojoCodec ser/deser - Iterations:" + ITERATIONS + " Duration (ms):" + TEST_DURATION_MILLIS);
	}


	@Test
	public void runBenchmark() throws IOException {
		PojoCodec codec = getPojoCodec();
		Object objectToSerialize = getObjectToSerialize();
		Assert.notNull(objectToSerialize, "object to serialized cannot be null.");
		Assert.notNull(codec, "codec cannot be null.");
		String className = objectToSerialize.getClass().getName();
		warmup(codec, objectToSerialize);
		doGc();
		warmup(codec, objectToSerialize, 3);
		doGc();
		runSerializationBenchmark("serialize " + className, codec, objectToSerialize);
		doGc();
		runDeserializationBenchmark("deserialize " + className, codec, objectToSerialize);
		report();
	}

	void report() {
		System.out.println(stopWatch.prettyPrint());
		for (StopWatch.TaskInfo taskInfo : stopWatch.getTaskInfo()) {
			String taskName = taskInfo.getTaskName();
			if (!taskName.startsWith("warmup")) {
				double nanosecs = taskInfo.getTimeMillis() * 1000000.0;
				double averagens = nanosecs / counts.get(taskName);
				System.out.println(taskName + ": avg time (ns) " + averagens);
			}
		}
	}

	private void runSerializationBenchmark(String taskName, PojoCodec codec, Object object) throws IOException {
		final byte[] buffer = new byte[1024];

		stopWatch.start(taskName);
		long start = System.currentTimeMillis();
		int count = 0;
		while (System.currentTimeMillis() - start < TEST_DURATION_MILLIS) {
			for (int i = 0; i < ITERATIONS; i++) {

				final Output output = new Output(buffer, -1);
				codec.serialize(object, output);
				count++;
			}
		}
		stopWatch.stop();
		counts.put(taskName, count);
	}

	private void runDeserializationBenchmark(String taskName, PojoCodec codec, Object object) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		codec.serialize(object, bos);
		byte[] buffer = bos.toByteArray();
		int count = 0;
		stopWatch.start(taskName);
		long start = System.currentTimeMillis();
		count = 0;
		while (System.currentTimeMillis() - start < TEST_DURATION_MILLIS) {
			for (int i = 0; i < ITERATIONS; i++) {
				codec.deserialize(buffer, object.getClass());
				count++;
			}
		}
		stopWatch.stop();
		counts.put(taskName, count);
	}

	private void warmup(PojoCodec codec, Object obj) throws IOException {
		warmup(codec, obj, 10);
	}

	private void warmup(PojoCodec codec, Object obj, int seconds) throws IOException {
		//warm up
		long endTime = System.currentTimeMillis() + seconds * 1000;
		int i=0;
		do {
			runSerializationBenchmark("warmup ("+ seconds + " sec.) iteration:" + i , codec, obj);
			i++;
		} while (System.currentTimeMillis() < endTime);
	}

	// JVM is not required to honor GC requests, but adding bit of sleep around request is
	// most likely to give it a chance to do it.
	protected static void doGc() {
		try {
			Thread.sleep(50L);
		}
		catch (InterruptedException ie) {
			System.err.println("Interrupted while sleeping in doGc()");
		}
		System.gc();
		try { // longer sleep afterwards (not needed by GC, but may help with scheduling)
			Thread.sleep(200L);
		}
		catch (InterruptedException ie) {
			System.err.println("Interrupted while sleeping in doGc()");
		}
	}

	protected abstract PojoCodec getPojoCodec();

	protected abstract Object getObjectToSerialize();
}

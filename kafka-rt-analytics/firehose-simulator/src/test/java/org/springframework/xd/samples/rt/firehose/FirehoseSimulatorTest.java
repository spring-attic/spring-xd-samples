/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.xd.samples.rt.firehose;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import org.junit.Test;
import rx.Subscriber;

import org.springframework.xd.dirt.integration.bus.serializer.kryo.KryoClassListRegistrar;
import org.springframework.xd.dirt.integration.bus.serializer.kryo.PojoCodec;
import org.springframework.xd.samples.rt.event.AppEvent;
import org.springframework.xd.samples.rt.event.Event;

/**
 * @author David Turanski
 */
public class FirehoseSimulatorTest {


	private FirehoseSimulator firehose;


	@Test
	public void testDefault() throws InterruptedException, IOException {
		firehose = new FirehoseSimulator(10);
		firehose.generator().subscribe(new SourceEventSubscriber(10, "default"));
	}

	@Test
	public void testSingleAppAndEventType() throws InterruptedException, IOException {

		firehose = new FirehoseSimulator();
		firehose.setSourceDistribution(100);
		firehose.setEventDistribution(100);
		firehose.generator().subscribe(new SourceEventSubscriber(10, "singleAppAndEvent"));
	}

	@Test
	public void testSingleAppMultipeEventType() throws InterruptedException, IOException {

		firehose = new FirehoseSimulator();
		firehose.setSourceDistribution(100);
		firehose.generator().subscribe(new SourceEventSubscriber(10, "singleAppMultipleEvent"));
	}

	@Test
	public void testSkewedAppMultipeEventType() throws InterruptedException, IOException {

		firehose = new FirehoseSimulator();
		firehose.setSourceDistribution(70, 10, 5, 5);
		firehose.generator().subscribe(new SourceEventSubscriber(1000, "skewedApp"));
	}

	@Test
	public void testSkewedAppNonDescending() throws InterruptedException, IOException {

		firehose = new FirehoseSimulator();
		firehose.setSourceDistribution(10, 80, 5, 5);
		firehose.generator().subscribe(new SourceEventSubscriber(1000, "skewedApp"));
	}

	@Test
	public void testEventDistribution() throws InterruptedException, IOException {

		firehose = new FirehoseSimulator(1000);
		firehose.setEventDistribution(98, 1, 1);
		firehose.generator().subscribe(new SourceEventSubscriber(1000, "eventDistribution"));
	}


	@Test
	public void throughput() {
		firehose = new FirehoseSimulator();
		firehose.generator().subscribe(new BenchmarkSubscriber(10000, null));
	}

	@Test
	public void throughputWithSerialization() {
		firehose = new FirehoseSimulator();
		KryoClassListRegistrar registrar = new KryoClassListRegistrar(Collections.singletonList(AppEvent.class));
		final PojoCodec codec = new PojoCodec(/*registrar*/);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		firehose.generator().subscribe(new BenchmarkSubscriber(10000, e -> {
			try {
				outputStream.flush();
				codec.serialize(e, outputStream);
				byte[] bytes = outputStream.toByteArray();
				codec.deserialize(bytes, AppEvent.class);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}));
	}

	class BenchmarkSubscriber extends Subscriber<Event> {

		private final long durationMillisecs;

		private final Function<Event, ?> function;

		private int count;

		private long start;

		public BenchmarkSubscriber(long durationMillisecs, Function<Event, ?> function) {
			this.function = function;
			this.durationMillisecs = durationMillisecs;
		}

		@Override
		public void onCompleted() {

			System.out.println("generated " + count + " messages in " + durationMillisecs + " ms.");
			System.out.println("generated " + count * 1000.0 / durationMillisecs + " messages in 1000 ms.");
		}

		@Override
		public void onError(Throwable e) {

		}

		@Override
		public void onNext(Event event) {

			long timestamp = event.getTimestamp();
			if (function != null) {
				function.apply(event);
			}

			if (count == 0) {
				start = timestamp;
			}
			if (start + durationMillisecs >= timestamp) {
				count++;
			}
			else {
				firehose.stop();
			}
		}
	}

	class SourceEventSubscriber extends Subscriber<Event> {
		private final int maxSamples;

		private int count;

		private SortedMap<String, Integer> actuaSourceDistribution = new TreeMap<String, Integer>();

		private Map<String, Integer> actualEventDistribution = new HashMap<String, Integer>();

		private String description;

		public SourceEventSubscriber(int maxSamples, String description) {
			this.maxSamples = maxSamples;
			this.description = description;
		}

		@Override
		public void onNext(Event event) {
			String source = event.getSource();
			if (source == null) {
				System.out.println("source is null " + source);
				return;
			}

			if (!actuaSourceDistribution.containsKey(source)) {
				actuaSourceDistribution.put(source, 0);
			}
			int sourceCount = actuaSourceDistribution.get(source) + 1;
			actuaSourceDistribution.put(source, sourceCount);

			String eventTypeName = event.getEventType();

			if (!actualEventDistribution.containsKey(eventTypeName)) {
				actualEventDistribution.put(eventTypeName, 0);
			}
			int eventCount = actualEventDistribution.get(eventTypeName) + 1;
			actualEventDistribution.put(eventTypeName, eventCount);


			if (++count == maxSamples) {
				firehose.stop();
			}
		}

		@Override
		public void onCompleted() {
			System.out.println(description);
			for (Map.Entry<String, Integer> entry : actuaSourceDistribution.entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
			System.out.println();
			for (Map.Entry<String, Integer> entry : actualEventDistribution.entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
			System.out.println();
		}

		@Override
		public void onError(Throwable e) {
		}
	}
}
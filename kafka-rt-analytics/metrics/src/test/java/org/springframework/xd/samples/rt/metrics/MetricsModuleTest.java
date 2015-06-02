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

package org.springframework.xd.samples.rt.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xd.samples.rt.event.Event;
import org.springframework.xd.samples.rt.event.SourceEventBucket;
import org.springframework.xd.samples.rt.event.EventFactory;
import org.springframework.xd.samples.rt.metrics.config.MetricsConfiguration;
import org.springframework.xd.tuple.Tuple;

/**
 * @author David Turanski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MetricsConfiguration.class})
public class MetricsModuleTest {

	@Autowired
	private MetricsService metricsService;

	@Autowired
	MessageChannel input;

	@Autowired
	MessageChannel output;

	private SourceEventBucket bucket;

	private String source;

	private long startTime;

	@Before
	public void setUp() {
		source = "source0";
		startTime = new Date().getTime();

		long startTime = new Date().getTime();
		Event event1 = EventFactory.httpRequest(source, startTime - 1234, startTime, 200);
		Event event2 = EventFactory.httpRequest(source, startTime - 100, startTime  + 100, 404);
		Event event3 = EventFactory.logMessage(source, startTime + 500, "this is a log message", "ERROR");

		bucket = new SourceEventBucket(source, startTime, 1000);
		bucket.addEvent(event1);
		bucket.addEvent(event2);
		bucket.addEvent(event3);
	}

	@Test
	public void testStandalone() {
		List<Tuple> metrics = metricsService.calculateMetrics(bucket);
		verify(metrics);
	}


	@Test
	public void testWithIntegration() {
		final AtomicBoolean received = new AtomicBoolean();

		((SubscribableChannel) output).subscribe(message -> {
			List<Tuple> metrics = (List<Tuple>) message.getPayload();
			received.set(true);
			verify(metrics);
		});

		input.send(MessageBuilder.withPayload(bucket).build());
		assertTrue("no message received", received.get());
	}

	private void verify(List<Tuple> metrics) {
		assertEquals(4, metrics.size());

		for (Tuple tuple : metrics) {
			if (tuple.getString(Metric.METRIC_NAME).equals("eventCount")) {
				int count = tuple.getInt(Metric.METRIC_VALUE);
				assertEquals("event count is wrong", 3, count);
				assertEquals(startTime, tuple.getLong(Metric.BUCKET_TIMESTAMP));
				assertEquals(source, tuple.getString(Metric.SOURCE));
				assertEquals(1000, tuple.getLong(Metric.BUCKET_DURATION));
			}
			else if (tuple.getString(Metric.METRIC_NAME).equals("httpResponseTime")) {
				int responseTime = tuple.getInt(Metric.METRIC_VALUE);
				assertEquals("response time is wrong", 1234, responseTime);
				assertEquals(startTime, tuple.getLong(Metric.BUCKET_TIMESTAMP));
				assertEquals(source, tuple.getString(Metric.SOURCE));
				assertEquals(1000, tuple.getLong(Metric.BUCKET_DURATION));

			}
			else if (tuple.getString(Metric.METRIC_NAME).equals("httpErrorCount")) {
				int responseTime = tuple.getInt(Metric.METRIC_VALUE);
				assertEquals(startTime, tuple.getLong(Metric.BUCKET_TIMESTAMP));
				assertEquals(source, tuple.getString(Metric.SOURCE));
				assertEquals(1000, tuple.getLong(Metric.BUCKET_DURATION));
			}
			else if (tuple.getString(Metric.METRIC_NAME).equals("errorMessageCount")) {
				int count = tuple.getInt(Metric.METRIC_VALUE);
				assertEquals("event count is wrong", 1, count);
				assertEquals(startTime, tuple.getLong(Metric.BUCKET_TIMESTAMP));
				assertEquals(source, tuple.getString(Metric.SOURCE));
				assertEquals(1000, tuple.getLong(Metric.BUCKET_DURATION));
			}
		}
	}
}

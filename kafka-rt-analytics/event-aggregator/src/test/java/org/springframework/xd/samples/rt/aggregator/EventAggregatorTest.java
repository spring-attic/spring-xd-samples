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

package org.springframework.xd.samples.rt.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xd.samples.rt.aggregator.config.AggregatorConfiguration;
import org.springframework.xd.samples.rt.event.Event;
import org.springframework.xd.samples.rt.event.EventFactory;
import org.springframework.xd.samples.rt.event.SourceEventBucket;
import org.springframework.xd.samples.rt.firehose.FirehoseSimulator;

/**
 * @author David Turanski
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EventAggregatorTestConfig.class})
public class EventAggregatorTest {
	@Autowired
	MessageChannel input;

	@Autowired
	MessageChannel output;

	@Test
	@DirtiesContext
	public void test() throws InterruptedException {

		final AtomicInteger receivedEventCount = new AtomicInteger();
		final AtomicInteger sentMessageCount = new AtomicInteger();

		((SubscribableChannel) output).subscribe(message -> {
			SourceEventBucket bucket = (SourceEventBucket) message.getPayload();
			System.out.println(bucket.getSource() + " " + bucket.getfirstEventTime() + " " + bucket.getEvents().size
					());
			receivedEventCount.getAndAdd(bucket.getEvents().size());
			for (Event e : bucket.getEvents()) {
				long ts = e.getTimestamp();
				assertTrue(String.format("start: %d, stop: %d, ts: %d", bucket.getfirstEventTime(),
								bucket.getDuration(),
								ts),
						bucket.isInWindow(ts));
			}
		});

		FirehoseSimulator firehoseSimulator = new FirehoseSimulator(1000);
		firehoseSimulator.setSourceDistribution(25, 25, 25, 25);

		firehoseSimulator.generator().subscribe(new Subscriber<Event>() {
			private long startTime;

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable throwable) {
				throwable.printStackTrace();
			}

			@Override
			public void onNext(Event event) {
				long eventTime = event.getTimestamp();
				if (startTime == 0) {
					startTime = eventTime;
				}
				input.send(new GenericMessage<Event>(event));
				sentMessageCount.getAndIncrement();
				if (eventTime - startTime > 1500) {
					firehoseSimulator.stop();
				}
			}
		});
		Thread.sleep(5500);
		assertEquals("not all messages received", sentMessageCount.get(), receivedEventCount.get());
	}

	@Test
	@DirtiesContext
	public void testTwoMessages() {

		final AtomicInteger receivedEventCount = new AtomicInteger();

		((SubscribableChannel) output).subscribe(message -> {
			SourceEventBucket bucket = (SourceEventBucket) message.getPayload();
			System.out.println(bucket.getSource() + " " + bucket.getfirstEventTime() + " " + bucket.getEvents().size
					());
			receivedEventCount.getAndAdd(bucket.getEvents().size());
			for (Event e : bucket.getEvents()) {
				long ts = e.getTimestamp();
				assertTrue(String.format("start: %d, stop: %d, ts: %d", bucket.getfirstEventTime(),
								bucket.getDuration(),
								ts),
						bucket.isInWindow(ts));
			}
		});

		String source = "source0";
		long startTime = new Date().getTime();
		Event event1 = EventFactory.httpRequest(source, startTime, startTime + 1234, 200);

		//2 sec later
		Event event2 = EventFactory.httpRequest(source, startTime + 2000, startTime + 2000 + 1234, 200);


		input.send(new GenericMessage<Event>(event1));
		input.send(new GenericMessage<Event>(event2));

		assertEquals("no messages received", 1, receivedEventCount.get());
	}

}

@Configuration
@Import(AggregatorConfiguration.class)
@EnableScheduling
class EventAggregatorTestConfig {
}
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

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xd.samples.rt.firehose.config.FirehoseSimulatorConfiguration;

/**
 * @author David Turanski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class})
public class FirehoseDriverTest {

	@Autowired
	private FirehoseSimulatorDriver firehoseSimulatorDriver;

	@Autowired
	private SubscribableChannel output;

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void test() throws InterruptedException {
		final AtomicInteger count = new AtomicInteger();
		output.subscribe(message -> {
			System.out.println(message);
			if (count.getAndIncrement() == 10) {
				firehoseSimulatorDriver.stop();
			}
		});
		firehoseSimulatorDriver.start();
		Thread.sleep(1000);
	}

	@Test

	public void testDistributionLoader() throws InterruptedException {
		applicationContext.getBean(TaskScheduler.class);
		applicationContext.getBean(DistributionLoader.class);
		FirehoseSimulator firehoseSimulator = firehoseSimulatorDriver.getFirehoseSimulator();
		Thread.sleep(5100);
		assertNotNull(firehoseSimulator.getSourceDistribution());

	}

	@Test
	public void throughput() throws InterruptedException {
		long start = new Date().getTime();
		final AtomicInteger count = new AtomicInteger();
		output.subscribe(new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				//System.out.println(message);
				count.getAndIncrement();
			}
		});
		firehoseSimulatorDriver.start();
		Thread.sleep(5000);
		firehoseSimulatorDriver.stop();
		long stop = new Date().getTime();
		System.out.println("received " + count.get() + " messages in " + (stop - start));
		Double x = count.get() * 1000.0 / (stop - start);
		System.out.println(String.format("that's %.2f msg/sec", x));
	}
}

@Configuration
@Import(FirehoseSimulatorConfiguration.class)
class TestContext {
	@Bean
	PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertyPlaceholderConfigurer();
	}

}



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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.xd.dirt.test.process.SingleNodeProcessingChainSupport.chain;

import java.util.Date;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.messaging.support.GenericMessage;
import org.springframework.xd.samples.rt.event.Event;
import org.springframework.xd.samples.rt.event.SourceEventBucket;

import org.springframework.xd.dirt.server.SingleNodeApplication;
import org.springframework.xd.dirt.test.SingleNodeIntegrationTestSupport;
import org.springframework.xd.dirt.test.SingletonModuleRegistry;
import org.springframework.xd.dirt.test.process.SingleNodeProcessingChain;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.test.RandomConfigurationSupport;

/**
 * @author David Turanski
 */
public class AggregatorModuleIntegrationTest {
	private static SingleNodeApplication application;

	private static int RECEIVE_TIMEOUT = 5000;

	/**
	 * Start the single node container, binding random unused ports, etc. to not conflict with any other instances
	 * running on this host. Configure the ModuleRegistry to include the project module.
	 */
	@BeforeClass
	public static void setUp() {
		//System.setProperty("XD_HOME","/Users/dturanski/s2build/spring-xd/build/dist/spring-xd/xd");
		RandomConfigurationSupport randomConfigSupport = new RandomConfigurationSupport();
		application = new SingleNodeApplication().run();
		SingleNodeIntegrationTestSupport singleNodeIntegrationTestSupport = new SingleNodeIntegrationTestSupport
				(application);
		singleNodeIntegrationTestSupport.addModuleRegistry(new SingletonModuleRegistry(ModuleType.processor,
				"bucket-aggregator"));
	}

	@Test
	public void testTwoMessagesForOneSource() {
		SingleNodeProcessingChain chain = chain(application, "aggregatorStream", "bucket-aggregator");
		String source = UUID.randomUUID().toString();
		long startTime = new Date().getTime();
		Event e1 = new Event(source,"httpStartStop", startTime, null);
		Event e2 = new Event(source,"httpStartStop", startTime + 100, null);
		chain.send(new GenericMessage<Event>(e1));
		chain.send(new GenericMessage<Event>(e2));
		Object payload = chain.receivePayload(RECEIVE_TIMEOUT);
		assertNotNull("no message received", payload);
		assertTrue("wrong type " + payload.getClass().getName(), payload instanceof SourceEventBucket);
		assertEquals("WTF", 2,((SourceEventBucket)payload).getEvents().size());
		chain.destroy();
	}

}

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
package org.springframework.xd.samples.conversion;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.xd.dirt.server.SingleNodeApplication;
import org.springframework.xd.dirt.test.SingleNodeIntegrationTestSupport;
import org.springframework.xd.dirt.test.SingletonModuleRegistry;
import org.springframework.xd.dirt.test.process.SingleNodeProcessingChain;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.test.RandomConfigurationSupport;
import org.springframework.xd.tuple.DefaultTuple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.xd.dirt.test.process.SingleNodeProcessingChainSupport.chain;

/**
 * @author Glenn Renfro
 */
public class MyTupleProcessorTest {
	private static SingleNodeApplication application;

	private static int RECEIVE_TIMEOUT = 5000;


	/**
	 * Start the single node container, binding random unused ports, etc. to not conflict with any other instances
	 * running on this host. Configure the ModuleRegistry to include the project module.
	 */
	@BeforeClass
	public static void setUp() {
		RandomConfigurationSupport randomConfigSupport = new RandomConfigurationSupport();
		application = new SingleNodeApplication().run();
		SingleNodeIntegrationTestSupport singleNodeIntegrationTestSupport = new SingleNodeIntegrationTestSupport
				(application);
		singleNodeIntegrationTestSupport.addModuleRegistry(new SingletonModuleRegistry(ModuleType.processor,
				"myTupleProcessor"));

	}

	@Test
	public void test() {
		String streamName = "payloadTest";
		String json = "{\"symbol\":\"FAKE\",\"price\":75}" ;

		String processingChainUnderTest = "myTupleProcessor --inputType=application/x-xd-tuple";

		SingleNodeProcessingChain chain = chain(application, streamName, processingChainUnderTest);

		chain.sendPayload(json);

		DefaultTuple result = (DefaultTuple) chain.receivePayload(RECEIVE_TIMEOUT);

		assertTrue(result instanceof DefaultTuple);
		assertEquals(result.getString("symbol"), "FAKE");
		//Unbind the source and sink channels from the message bus
		chain.destroy();
	}
}

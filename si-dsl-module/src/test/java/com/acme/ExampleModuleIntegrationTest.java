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
package com.acme;

import static org.junit.Assert.*;
import static org.springframework.xd.dirt.test.process.SingleNodeProcessingChainSupport.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.xd.dirt.plugins.ModuleConfigurationException;
import org.springframework.xd.dirt.server.SingleNodeApplication;
import org.springframework.xd.dirt.test.SingleNodeIntegrationTestSupport;
import org.springframework.xd.dirt.test.SingletonModuleRegistry;
import org.springframework.xd.dirt.test.process.SingleNodeProcessingChain;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.test.RandomConfigurationSupport;


/**
 * Unit tests a module deployed to an XD single node container.
 */
public class ExampleModuleIntegrationTest {

	private static SingleNodeApplication application;

	private static int RECEIVE_TIMEOUT = 5000;

	private static String moduleName = "siDslModule";

	SingleNodeProcessingChain chain;

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
				moduleName));

	}

	/**
	 * Each test creates a stream with the module under test, or in general a "chain" of processors. The
	 * SingleNodeProcessingChain is a test fixture that allows the test to send and receive messages to verify each
	 * message is processed as expected.
	 */
	@Test
	public void testDefault() {
		String prefix = "foo";
		String suffix = "bar";
		String streamName = "testDefault";


		String processingChainUnderTest = String.format("%s --prefix=%s --suffix=%s", moduleName, prefix, suffix);

		chain = chain(application, streamName, processingChainUnderTest);

		chain.sendPayload("hello");
		String result = (String) chain.receivePayload(RECEIVE_TIMEOUT);
		assertEquals(prefix + "hello" + suffix, result);
	}

	@Test
	public void testPrefixOnly() {
		String prefix = "foo";
		String suffix = "bar";
		String streamName = "testPrefixOnly";

		String processingChainUnderTest = String.format("%s --prefix=%s --prefixOnly=true", moduleName, prefix);

		chain = chain(application, streamName, processingChainUnderTest);

		chain.sendPayload("hello");
		String result = (String) chain.receivePayload(RECEIVE_TIMEOUT);
		assertEquals(prefix + "hello", result);
	}

	@Test(expected = ModuleConfigurationException.class)
	public void testPrefixAndSuffixCannotBeTheSame() throws Exception {
		String prefix = "foo";
		String suffix = "foo";
		String streamName = "testPrefixNotSameAsSuffix";

		String processingChainUnderTest = String.format("%s --prefix=%s --suffix=%s", moduleName, prefix, suffix);
		chain = chain(application, streamName, processingChainUnderTest);
	}

	/**
	 * Destroy the chain to reset message bus bindings and destroy the stream.
	 */
	@After
	public void tearDown() {
		if (chain != null) {
			chain.destroy();
		}
	}
}

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

package com.acme;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * @author David Turanski
 */
public class ModuleConfigurationTest {
	@Test
	public void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		Properties properties = new Properties();
		properties.put("prefix","foo");
		properties.put("suffix","bar");
		context.getEnvironment().getPropertySources().addLast(new PropertiesPropertySource("options", properties));
		context.register(TestConfiguration.class);
		context.refresh();

		MessageChannel input = context.getBean("input", MessageChannel.class);
		SubscribableChannel output = context.getBean("output", SubscribableChannel.class);

		final AtomicBoolean handled = new AtomicBoolean();
		output.subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				handled.set(true);
				assertEquals("foohellobar", message.getPayload());
			}
		});
		input.send(new GenericMessage<String>("hello"));
		assertTrue(handled.get());
	}

	@Configuration
	@Import(ModuleConfiguration.class)
	static class TestConfiguration {
		@Bean
		public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
			PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new
					PropertySourcesPlaceholderConfigurer();
			return propertySourcesPlaceholderConfigurer;
		}
	}
}

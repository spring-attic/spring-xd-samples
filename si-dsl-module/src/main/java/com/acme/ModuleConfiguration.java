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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;

/**
 * @author David Turanski
 */
@Configuration
@EnableIntegration
@Import({PrefixAndSuffixConfiguration.class, PrefixOnlyConfiguration.class})
public class ModuleConfiguration {
	@Autowired
	GenericTransformer<String,String> transformer;

	@Bean
	public MessageChannel input() {
		return new DirectChannel();
	}

	@Bean
	MessageChannel output() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow myFlow() {
		return IntegrationFlows.from(this.input())
				.transform(transformer)
				.channel(this.output())
				.get();
	}
}

@Configuration
@Profile({"use-both","default"})
class PrefixAndSuffixConfiguration {

	@Value("${prefix}")
	private String prefix;

	@Value("${suffix:}")
	private String suffix;

	@Bean
	GenericTransformer<String, String> transformer() {
		return new GenericTransformer<String, String>() {
			@Override
			public String transform(String payload) {
				return prefix + payload + suffix;
			}
		};
	}
}

@Configuration
@Profile("use-prefix")
class PrefixOnlyConfiguration {

	@Value("${prefix}")
	private String prefix;

	@Bean
	GenericTransformer<String, String> transformer() {
		return new GenericTransformer<String, String>() {
			@Override
			public String transform(String payload) {
				return prefix + payload;
			}
		};
	}
}


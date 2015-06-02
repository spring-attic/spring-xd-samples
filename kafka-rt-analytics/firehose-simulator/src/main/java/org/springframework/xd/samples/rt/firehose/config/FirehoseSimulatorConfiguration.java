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

package org.springframework.xd.samples.rt.firehose.config;

import org.springframework.xd.samples.rt.firehose.DistributionLoader;
import org.springframework.xd.samples.rt.firehose.FirehoseSimulator;
import org.springframework.xd.samples.rt.firehose.FirehoseSimulatorDriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.StringUtils;

/**
 * @author David Turanski
 */
@Configuration
@EnableIntegration
@EnableScheduling
public class FirehoseSimulatorConfiguration implements SchedulingConfigurer {

	@Value("${numSources:10000}")
	int numSources;

	@Value("${sourceDistribution:}")
	String sourceDistribution;

	@Value("${eventDistribution:}")
	String eventDistribution;

	@Autowired
	ApplicationContext context;

	@Bean
	FirehoseSimulatorDriver firehoseSimulatorDriver() {
		FirehoseSimulatorDriver driver = new FirehoseSimulatorDriver(firehoseSimulator());
		driver.setOutputChannel(output());
		return driver;
	}

	@Bean
	MessageChannel output() {
		return new DirectChannel();
	}

	@Bean
	FirehoseSimulator firehoseSimulator() {
		FirehoseSimulator firehoseSimulator = numSources > 0 ? new FirehoseSimulator(numSources) : new FirehoseSimulator();

		if (StringUtils.hasText(sourceDistribution)) {
			firehoseSimulator.setSourceDistribution(parse(sourceDistribution));
		}

		if (StringUtils.hasText(eventDistribution)) {
			firehoseSimulator.setEventDistribution(parse(eventDistribution));
		}
		return firehoseSimulator;
	}

	private int[] parse(String distribution) {
		String[] tokens = distribution.split(",");
		int[] percentages = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			percentages[i] = Integer.parseInt(tokens[i]);
		}
		return percentages;
	}

	@Bean
	DistributionLoader distributionLoader() {
		DistributionLoader distributionLoader = new DistributionLoader(firehoseSimulator(),
				("classpath:/firehose.properties"));
		return distributionLoader;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		scheduledTaskRegistrar.setScheduler(context.getBean(IntegrationContextUtils.TASK_SCHEDULER_BEAN_NAME, TaskScheduler.class));
	}
}

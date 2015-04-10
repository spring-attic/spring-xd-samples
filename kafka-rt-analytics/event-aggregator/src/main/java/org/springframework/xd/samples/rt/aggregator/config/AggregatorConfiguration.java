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

package org.springframework.xd.samples.rt.aggregator.config;

import org.springframework.xd.samples.rt.aggregator.BucketReleaseTask;
import org.springframework.xd.samples.rt.aggregator.EventAggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author David Turanski
 */
@Configuration
@EnableIntegration
@EnableScheduling
public class AggregatorConfiguration implements SchedulingConfigurer {
	
	@Autowired
	ApplicationContext context;


	@Bean
	public MessageChannel output() { return new DirectChannel();}
	
	@Bean
	public MessageChannel input() {
		return new DirectChannel();
	}
	
	@Bean 
	@ServiceActivator(inputChannel = "input", outputChannel = "output")
	public EventAggregator eventAggregator() {
		return new EventAggregator();
	}
	
	@Bean
	public BucketReleaseTask bucketReleaseTask() {
		BucketReleaseTask bucketReleaseTask = new BucketReleaseTask();
		bucketReleaseTask.setOutputChannel(output());
		bucketReleaseTask.setAppEvents(eventAggregator().getEventsBySource());
		return bucketReleaseTask;
	}
	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		scheduledTaskRegistrar.setScheduler(context.getBean(IntegrationContextUtils.TASK_SCHEDULER_BEAN_NAME, TaskScheduler.class));
	}
}

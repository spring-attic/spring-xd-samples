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

package org.springframework.xd.samples.rt.metrics.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.xd.samples.rt.metrics.Metric;
import org.springframework.xd.samples.rt.metrics.MetricsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

/**
 * @author David Turanski
 */
@Configuration
@EnableIntegration
@ComponentScan(basePackages = {"org.springframework.xd.samples.rt.metrics"})
public class MetricsConfiguration {

	@Autowired
	ApplicationContext applicationContext;

	@Bean
	public MessageChannel output() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel input() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "input", outputChannel = "output")
	public MetricsService appMetricsService() {
		Map<String, Metric> appMetricMap = applicationContext.getBeansOfType(Metric.class);
		List<Metric> appMetrics = new ArrayList<>(appMetricMap.values());
		MetricsService appMetricsService = new MetricsService(appMetrics);
		return appMetricsService;
	}
}

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

package org.springframework.xd.samples.rt.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xd.samples.rt.aggregator.EventAggregator;
import org.springframework.xd.samples.rt.event.Event;
import org.springframework.xd.samples.rt.event.SourceEventBucket;
import org.springframework.xd.samples.rt.firehose.FirehoseSimulator;
import org.springframework.xd.tuple.Tuple;

/**
 * @author David Turanski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class MetricsFirehoseTest {

	@Autowired
	private MetricsService appMetricsService;

	@Autowired
	private EventAggregator aggregator;

	@Test
	public void test() {
		FirehoseSimulator firehoseSimulator = new FirehoseSimulator(100);

		firehoseSimulator.generator().subscribe(new Subscriber<Event>() {
			int max = 1000;

			int count = 0;

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable throwable) {
				System.out.println("error");
			}

			@Override
			public void onNext(Event event) {
				SourceEventBucket bucket = aggregator.aggregate(event);
				List<Tuple> metrics = null;
				if (bucket != null) {
					metrics = appMetricsService.calculateMetrics(bucket);
				}

				if (metrics != null) {
					count++;
				}
				if (count == max) {
					firehoseSimulator.stop();
				}
			}
		});
	}
}


@Configuration
@ComponentScan(basePackages = {"org.springframework.xd.samples.rt.metrics"})
class TestConfig {

	@Autowired
	ApplicationContext applicationContext;

	@Bean
	public MetricsService appMetricsService() {
		Map<String, Metric> appMetricMap = applicationContext.getBeansOfType(Metric.class);
		List<Metric> appMetrics = new ArrayList<>(appMetricMap.values());
		MetricsService appMetricsService = new MetricsService(appMetrics);
		return appMetricsService;
	}

	@Bean
	public EventAggregator eventAggregator() {
		return new EventAggregator();
	}
}



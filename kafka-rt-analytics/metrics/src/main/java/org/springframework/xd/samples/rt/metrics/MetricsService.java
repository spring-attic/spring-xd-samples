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

import org.springframework.xd.samples.rt.event.SourceEventBucket;
import org.springframework.xd.tuple.Tuple;

/**
 * @author David Turanski
 */
public class MetricsService {
	private final List<Metric> appMetrics;

	public MetricsService(List<Metric> appMetrics) {
		this.appMetrics = appMetrics;
	}
	
	public List<Tuple> calculateMetrics(SourceEventBucket bucket) {
		List<Tuple> results = new ArrayList<>();
		for (Metric appMetric: appMetrics){
			Tuple t = appMetric.calculate(bucket);
			if (t != null) {
				results.add(appMetric.calculate(bucket));
			}
		}
		return results;
	}
}

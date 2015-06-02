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

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.xd.samples.rt.event.AppEvent;
import org.springframework.xd.samples.rt.event.Event;

/**
 * Average Http Response Time for successful requests.
 *
 * @author David Turanski
 */
@Component
public class AverageHttpResponseTime extends AbstractMetric<Double> {

	public AverageHttpResponseTime() {
		super("httpResponseTime");
	}

	@Override
	protected Double calculateMetric(List<Event> events) {
		Double result = 0.0;
		int count = 0;
		for (Event event : events) {
			if (event.getEventType().equals(AppEvent.EventType.httpRequest.name()) && (int) event.getData().get("statusCode")
					== 200) {
				long startTime = (long) event.getData().get("startTime");
				long endTime = event.getTimestamp();
				result += (endTime - startTime);
				count++;
			}
		}
		return (count == 0) ? null : result / count;
	}
}

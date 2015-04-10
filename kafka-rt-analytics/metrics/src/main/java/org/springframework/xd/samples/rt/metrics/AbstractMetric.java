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

import java.util.Arrays;
import java.util.List;

import org.springframework.xd.samples.rt.event.Event;
import org.springframework.xd.samples.rt.event.SourceEventBucket;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author David Turanski
 */
public abstract class AbstractMetric<T> implements Metric {
	private String name;

	private T value;

	private long timestamp;

	private long duration;

	private String source;

	protected AbstractMetric(String name) {
		this.name = name;
	}


	private List<String> names = Arrays.asList(new String[] {METRIC_NAME, SOURCE, METRIC_VALUE, BUCKET_TIMESTAMP,
			BUCKET_DURATION});

	@Override
	public Tuple calculate(SourceEventBucket bucket) {
		copyBucketMetadata(bucket);
		value = calculateMetric(bucket.getEvents());
		if (value == null) {
			return null;
		}
		List<Object> values = Arrays.asList(new Object[] {name, source, value, timestamp, duration});
		return TupleBuilder.tuple().ofNamesAndValues(names, values);
	}

	protected abstract T calculateMetric(List<Event> events);

	private void copyBucketMetadata(SourceEventBucket bucket) {
		timestamp = bucket.getfirstEventTime();
		source = bucket.getSource();
		duration = bucket.getDuration();
	}
}

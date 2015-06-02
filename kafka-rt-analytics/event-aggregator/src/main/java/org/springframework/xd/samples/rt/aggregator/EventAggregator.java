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

package org.springframework.xd.samples.rt.aggregator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.xd.samples.rt.event.Event;
import org.springframework.xd.samples.rt.event.SourceEventBucket;

/**
 * Collects events in {@link org.springframework.xd.samples.rt.event.SourceEventBucket} mapped by source and returns
 * buckets that have received all events over the bucket duration window.
 *
 * @author David Turanski
 */
public class EventAggregator {

	private final long bucketDuration;

	/**
	 * *
	 *
	 * @param bucketDuration bucket window size in miliseconds
	 */
	public EventAggregator(long bucketDuration) {
		this.bucketDuration = bucketDuration;

	}

	public EventAggregator() {
		this(1000);
	}

	static final Logger log = LoggerFactory.getLogger(EventAggregator.class);

	Map<String, SourceEventBucket> eventsBySource = new ConcurrentHashMap<>();

	public SourceEventBucket aggregate(Event event) {

		String source = event.getSource();
		long timestamp = event.getTimestamp();
		log.debug("received event for source {} timestamp(ms) {}", event.getSource(), timestamp);
		SourceEventBucket bucket;
		synchronized (eventsBySource) {
			if (!eventsBySource.containsKey(source)) {
				bucket = new SourceEventBucket(source, timestamp, bucketDuration);
				eventsBySource.put(source, bucket);
			}
			bucket = eventsBySource.get(source);
		}
		
		if (!bucket.addEvent(event)) {
			eventsBySource.remove(source);
			SourceEventBucket newBucket = new SourceEventBucket(source, timestamp, bucketDuration);
			eventsBySource.put(source, newBucket);
			newBucket.addEvent(event);
			log.debug("releasing events for source {}", source);
			return bucket;
		}


		return null;
	}

	public Map<String, SourceEventBucket> getEventsBySource() {
		return this.eventsBySource;
	}

}

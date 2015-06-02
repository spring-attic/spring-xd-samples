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

import java.util.Date;
import java.util.Map;

import org.springframework.xd.samples.rt.event.SourceEventBucket;

import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Runs as a scheduled task every 5 seconds. Release any buckets whose last event is older than the buck duration.
 *
 * @author David Turanski
 */
public class BucketReleaseTask implements MessageProducer {

	private MessageChannel messageChannel;

	private Map<String, SourceEventBucket> eventsBySource;

	@Scheduled(fixedDelay = 5000)
	public void releaseBuckets() {
		long now = new Date().getTime();
		for (SourceEventBucket bucket : eventsBySource.values()) {
			if (now - bucket.getLastEventTime() >= bucket.getDuration()) {
				messageChannel.send(
						new GenericMessage<SourceEventBucket>(eventsBySource.remove(bucket.getSource())));
			}
		}
	}

	@Override
	public void setOutputChannel(MessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}

	public void setAppEvents(Map<String, SourceEventBucket> eventsBySource) {
		this.eventsBySource = eventsBySource;

	}
}

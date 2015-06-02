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

package org.springframework.xd.samples.rt.event;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple domain class for creating an arbitrary event.
 *
 * @author David Turanski
 */
public class Event {

	private final String source;

	private final String eventType;

	private final long timestamp;

	private final Map<String,Object> data;

	public Event(String source, String eventType, long timestamp, Map<String,Object> data) {
		this.source = source;
		this.eventType = eventType;
		this.timestamp = timestamp;
		this.data = data;
	}

	public String getSource() {
		return source;
	}

	public String getEventType() {
		return eventType;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Map<String,Object> getData() {
		return data;
	}

	@Override
	public synchronized String toString() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(this);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}

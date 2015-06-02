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

package org.springframework.xd.samples.rt.event;

import java.util.LinkedList;
import java.util.List;

/**
 * @author David Turanski
 */
public class SourceEventBucket {
	private final String source;

	private final long startTime;
	
	private long endTime;

	private final long duration;

	private final List<Event> events;


	public SourceEventBucket(String source, long startTime, long duration) {
		this.source = source;
		this.startTime = startTime;
		this.duration = duration;
		this.events = new LinkedList<>();
	}

	public String getSource() {
		return source;
	}

	public long getfirstEventTime() {
		return startTime;
	}

	public long getLastEventTime() {
		return endTime;
	}

	public List<Event> getEvents() {
		return events;
	}

	public long getDuration() {
		return duration;
	}

	public boolean isInWindow(long timestamp) {
		return timestamp >= startTime && timestamp <= (startTime + duration);
	}

	public boolean addEvent(Event event) {
		endTime = event.getTimestamp();
		if (isInWindow(endTime)) {
			this.events.add(event);
			return true;
		}
		return false;
	}

}
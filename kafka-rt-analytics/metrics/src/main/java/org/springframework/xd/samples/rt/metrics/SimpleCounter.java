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
import java.util.function.Predicate;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.xd.samples.rt.event.Event;

/**
 * @author David Turanski
 */
public class SimpleCounter extends AbstractMetric<Integer> {
	private final Predicate<Event> predicate;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	protected SimpleCounter(String name, Predicate<Event> predicate) {
		super(name);
		this.predicate = predicate;
	}

	@Override
	protected Integer calculateMetric(List<Event> events) {
		int count = 0;
		for (Event event : events) {
			if (predicate.test(event)) {
				log.debug("counting {}" , event);
				count++;
			}
		}
		return count == 0 ? null : count;
	}
}

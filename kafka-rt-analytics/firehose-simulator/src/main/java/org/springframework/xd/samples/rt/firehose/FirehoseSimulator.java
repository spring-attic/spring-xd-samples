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
package org.springframework.xd.samples.rt.firehose;

import static org.springframework.xd.samples.rt.event.AppEvent.EventType;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import rx.Observable;

import org.springframework.xd.samples.rt.event.Event;
import org.springframework.xd.samples.rt.event.EventFactory;


/**
 * @author David Turanski
 */
public class FirehoseSimulator {
	private final static int MAX_SOURCES = 10000;

	private final int NUM_SOURCES;

	private int[] sourceDistribution;

	private int[] eventDistribution;

	private final ArrayList<String> sources;


	Random random = new Random(new Date().getTime());

	boolean running;

	public FirehoseSimulator(int numSources) {
		NUM_SOURCES = numSources;
		sources = new ArrayList<>(NUM_SOURCES);
		init();
	}

	private void init() {
		for (int i = 0; i < NUM_SOURCES; i++) {
			sources.add("app" + i);
		}
	}

	public FirehoseSimulator() {
		this(MAX_SOURCES);
	}

	public void setSourceDistribution(int... percentages) {
		validateDistribution(percentages);
		sourceDistribution = percentages;
	}

	protected int[] getSourceDistribution() {
		return this.sourceDistribution;

	}

	public void setEventDistribution(int... percentages) {
		validateDistribution(percentages);
		eventDistribution = percentages;
	}

	protected int[] getEventDistribution() {
		return this.eventDistribution;
	}

	/**
	 * Generate weighted random data.
	 */
	public Observable<Event> generator() {

		Observable<Event> observable = Observable.create(subscriber -> {
			running = true;
			while (running) {
				subscriber.onNext(sourceEvent());
			}
			subscriber.onCompleted();
		});
		return observable;
	}

	private String getSource(int[] distribution) {
		return sources.get(weightedRandomIndex(distribution, NUM_SOURCES));
	}

	private EventType getEventType(int[] distribution) {
		return EventType.values()[weightedRandomIndex(distribution, EventType.values().length)];
	}

	private int weightedRandomIndex(int[] distribution, int range) {
		int index = random.nextInt(100);

		if (!(distribution == null || distribution.length == 0)) {
			int percent = 0;
			for (int i = 0; i < distribution.length; i++) {
				percent += distribution[i];
				if (index < percent) {
					return i;
				}
			}
		}
		int offset = distribution == null ? 0 : distribution.length;
		return random.nextInt(range - offset) + offset;

	}

	private Event sourceEvent() {
		String source = getSource(sourceDistribution);

		int[] httpStatusCodes = new int[] {200, 404, 403, 500};
		int[] httpStatusCodesWeights = new int[] {85,10,4,1};
		
		String[] logLevels = new String[]{"DEBUG","INFO","WARN","ERROR"};
		int[] logLevelWeights = new int[] {15,70,10,5};

		EventType eventType = getEventType(eventDistribution);
		long timestamp = new Date().getTime();

		switch (eventType) {
			case httpRequest:
				int code = weightedRandomIndex(httpStatusCodesWeights, httpStatusCodesWeights.length);
				// The event time is the endTime.
				return EventFactory.httpRequest(source, timestamp - + random.nextInt(8000) + 500,
					timestamp, httpStatusCodes[code]);
			case login:
				int user = random.nextInt();
				return EventFactory.login(source, timestamp, "user" + user, user%100 != 0);
			case logMessage:
				int level = weightedRandomIndex(logLevelWeights, logLevelWeights.length);
				return EventFactory.logMessage(source, timestamp, "this is a log message", logLevels[level]);
		}
		return null;
	}


	private void validateDistribution(int[] distribution) {
		if (distribution == null || distribution.length == 0) {
			return;
		}

		if (distribution.length > NUM_SOURCES) {
			throw new IllegalArgumentException(String.format(
					"invalid distribution buckets. Size must be <= %d", NUM_SOURCES));
		}

		int total = 0;
		for (int d : distribution) {
			total += d;
		}

		if (total <= 0 || total > 100) {
			throw new IllegalArgumentException(String.format(
					"invalid distribution buckets. total = %d. Total must be > 0 and <=100", total));
		}
	}

	public void stop() {
		running = false;
	}

	public void start() {
		running = true;
	}
}

/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acme.toptags.counter;

import static com.acme.toptags.common.Keys.COUNT;
import static com.acme.toptags.common.Keys.TAG;
import static java.util.concurrent.TimeUnit.SECONDS;

import rx.Observable;

import org.springframework.xd.rxjava.Processor;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author Marius Bogoevici
 */
public class RollingTagCounter implements Processor<String, Tuple> {

	private final int timeWindow;

	private final int timeShift;

	public RollingTagCounter(int timeWindow, int timeShift) {
		this.timeWindow = timeWindow;
		this.timeShift = timeShift;
	}


	@Override
	public Observable<Tuple> process(Observable<String> observable) {
		return observable.window(timeWindow, timeShift, SECONDS)
				.flatMap(
						// group by word
						w -> w.groupBy(e -> e)
								.flatMap(s -> Observable.zip(Observable.just(s.getKey()), s.count(), (a, b) -> TupleBuilder.tuple().of(TAG, a, COUNT, b)
										)
								)
				);
	}
}

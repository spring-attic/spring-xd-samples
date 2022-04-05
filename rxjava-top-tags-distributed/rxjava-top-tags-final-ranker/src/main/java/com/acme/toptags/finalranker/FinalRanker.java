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

package com.acme.toptags.finalranker;

import static com.acme.toptags.common.Keys.COUNT;
import static com.acme.toptags.common.Keys.RANKINGS;
import static com.acme.toptags.common.Keys.TAG;
import static com.acme.toptags.common.Keys.TOPTAGS;
import static org.springframework.xd.tuple.TupleBuilder.tuple;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.acme.toptags.common.TupleComparatorByCount;
import rx.Observable;

import org.springframework.xd.rxjava.Processor;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author Marius Bogoevici
 */
public class FinalRanker implements Processor<Tuple, Tuple> {

	private int timeWindow;

	private int timeShift;

	private int topN;

	public FinalRanker(int timeWindow, int timeShift, int topN) {
		this.timeWindow = timeWindow;
		this.timeShift = timeShift;
		this.topN = topN;
	}

	@Override
	public Observable<Tuple> process(Observable<Tuple> observable) {
		return observable.window(timeWindow, timeShift, TimeUnit.SECONDS)
				.flatMap(w ->
						// first, we merge all the intermediate rankings in a single stream
						w.flatMap(t -> Observable.from(t.getValue(RANKINGS, Tuple[].class)))
								// sort them
								.groupBy(e -> e.getString(TAG))
										// just take the last count emitted (which represents the latest update)
								.flatMap(s -> Observable.zip(
												Observable.just(s.getKey()),
												s.last().map(t -> t.getInt(COUNT)),
												(a, b) -> TupleBuilder.tuple().of(TAG, a, COUNT, b))
								)
								.toSortedList(TupleComparatorByCount.INSTANCE::compare)
										// take topN
								.map(l -> l.subList(0, Math.min(topN, l.size())))
								.map(l -> tuple().of(TOPTAGS, asMap(l))));
	}

	private static Map<String, Integer> asMap(List<Tuple> tuples) {
		Map<String, Integer> returnValue = new LinkedHashMap<>();
		for (Tuple tuple : tuples) {
			returnValue.put(tuple.getString(TAG), tuple.getInt(COUNT));
		}
		return returnValue;
	}
}

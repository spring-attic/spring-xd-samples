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

package com.acme.toptags.intermediate;

import static com.acme.toptags.common.Keys.COUNT;
import static com.acme.toptags.common.Keys.RANKINGS;
import static com.acme.toptags.common.Keys.TAG;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.math.operators.OperatorMinMax.max;

import com.acme.toptags.common.Keys;
import com.acme.toptags.common.TupleComparatorByCount;
import rx.Observable;

import org.springframework.xd.rxjava.Processor;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author Marius Bogoevici
 */
public class IntermediateRanker implements Processor<Tuple, Tuple> {

	public static final TupleComparatorByCount COMPARATOR = new TupleComparatorByCount();

	private final int timeWindow;

	private final int timeShift;

	private final int topN;

	public IntermediateRanker(int timeWindow, int timeShift, int topN) {
		this.timeWindow = timeWindow;
		this.timeShift = timeShift;
		this.topN = topN;
	}

	@Override
	public Observable<Tuple> process(Observable<Tuple> observable) {

		return observable
				// collect data every timeWindow seconds
				.window(timeWindow, timeShift, SECONDS)
				.flatMap(w ->
						// group data by word
						w.groupBy(e -> e.getString(TAG))
								// just take the last count emitted (which represents the latest update)
								.flatMap(s -> Observable.zip(
												Observable.just(s.getKey()),
												s.last().map(t -> t.getInt(COUNT)),
												(a, b) -> TupleBuilder.tuple().of(TAG, a, COUNT, b))
								)
										// rank words by count
								.toSortedList(TupleComparatorByCount.INSTANCE::compare)
										// merge word counts together
								.map(l -> l.subList(0, Math.min(topN, l.size())))
								.map(l -> TupleBuilder.tuple().of(RANKINGS, l.toArray(new Tuple[l.size()]))));
	}

}

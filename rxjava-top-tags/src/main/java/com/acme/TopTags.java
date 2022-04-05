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
package com.acme;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.xd.tuple.TupleBuilder.tuple;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.tuple.Tuples;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import rx.Observable;

import org.springframework.xd.rxjava.Processor;
import org.springframework.xd.tuple.Tuple;

/**
 * @author Marius Bogoevici
 */
public class TopTags implements Processor<String, Tuple> {

	private int timeWindow;

	private int timeShift;

	private int topN;

	public TopTags(int timeWindow, int timeShift, int topN) {
		this.timeWindow = timeWindow;
		this.timeShift = timeShift;
		this.topN = topN;
	}

	private static Log logger = LogFactory.getLog(TopTags.class);

	@Override
	public Observable<Tuple> process(Observable<String> inputStream) {
		return inputStream.flatMap(tweet -> {
			JSONArray array = JsonPath.read(tweet, "$.entities.hashtags[*].text");
			return Observable.from(array.toArray(new String[array.size()]));
		})
				// create (tag,1) tuple for each incoming tag
				.map(tag -> Tuples.pair(tag, 1))
						// batch all tags in the time window
				.window(timeWindow, timeShift, SECONDS)
						// with each time window stream
				.flatMap(windowBuffer ->
								windowBuffer
										// reduce by tag, counting all entries with the same tag
										.groupBy(Pair::getOne)
										.flatMap(
												groupedStream ->
														groupedStream.reduce((acc, v) -> Tuples.pair(acc.getOne(), acc.getTwo() + v.getTwo()))
										)
												// sort the results
										.toSortedList((a, b) -> -a.getTwo().compareTo(b.getTwo()))
												// convert the output to a friendlier format
										.map(l -> tuple().of("topTags",
														l.subList(0, Math.min(topN, l.size()))
																.stream().collect(Collectors.toMap(Pair::getOne, Pair::getTwo, (v1, v2) -> v1, LinkedHashMap::new)
														)
												)
										)
				);
	}
}

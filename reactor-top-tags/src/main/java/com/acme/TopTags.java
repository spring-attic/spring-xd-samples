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

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.util.StringUtils;
import org.springframework.xd.reactor.Processor;
import org.springframework.xd.tuple.Tuple;
import reactor.fn.Predicate;
import reactor.rx.BiStreams;
import reactor.rx.Stream;
import reactor.rx.Streams;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.xd.tuple.TupleBuilder.tuple;

/**
 * @author Mark Pollack
 */
public class TopTags implements Processor<String, Tuple> {

    private int timeWindow;

    private int topN;


    public TopTags(int timeWindow, int topN) {
        this.timeWindow = timeWindow;
        this.topN = topN;
    }

    @Override
    public Stream<Tuple> process(Stream<String> stream) {


        return stream.flatMap(tweet -> {
            JSONArray array = JsonPath.read(tweet, "$.entities.hashtags[*].text");
            return Streams.from(array.toArray(new String[array.size()]));
        })

                    .map(w -> reactor.fn.tuple.Tuple.of(w, 1))
                    .window(timeWindow, SECONDS)
                    .flatMap(s -> BiStreams.reduceByKey(s, (acc, next) -> acc + next)
                            .sort((a, b) -> -a.t2.compareTo(b.t2))
                            .take(topN))
                    .map(entry -> tuple().of("hashtag", entry.t1, "count", entry.t2));

    }
}

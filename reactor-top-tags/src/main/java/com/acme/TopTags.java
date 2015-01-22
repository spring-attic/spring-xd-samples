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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.xd.reactor.Processor;
import reactor.fn.tuple.Tuple;
import reactor.io.IOStreams;
import reactor.rx.BiStreams;
import reactor.rx.Stream;
import reactor.rx.Streams;
import reactor.rx.stream.MapStream;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Mark Pollack
 */
public class TopTags implements Processor<String, String> {

    private static final Logger LOG = LoggerFactory.getLogger(TopTags.class);
    // a Java Chronicle backed persistent map that auto delete on JVM shutdown for testing purposes
    // each update on the map will trigger subscriber signals
    MapStream<String, Integer> persistentMap;

    public TopTags() throws IOException {
        this.persistentMap = IOStreams.persistentMap("popularTags", true);
    }

    @Override
    public Stream<String> process(Stream<String> stream) {

        return stream
                .flatMap(tags -> Streams.from(tags.split(","))
                                        .filter(w -> !w.trim().isEmpty())
                )
                .map(w -> Tuple.of(w, 1))
                .window(1, SECONDS)
                .flatMap(s ->  BiStreams.reduceByKey(s, (acc, next) -> acc + next)
                                        .sort((a, b) -> -a.t2.compareTo(b.t2))
                                        .take(10)
                                        .finallyDo(_s -> LOG.info("------------------------ window complete! ----------------------"))
                )
                .map(entry -> entry.toString());
    }
}

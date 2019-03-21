/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.xd.rxjava.Processor;
import org.springframework.xd.tuple.Tuple;
import rx.Observable;
import rx.functions.Func1;


import java.util.List;

import static com.acme.Math.avg;
import static org.springframework.xd.tuple.TupleBuilder.tuple;

/**
 * @author Mark Pollack
 */
public class MovingAverage implements Processor<Tuple,Tuple> {

    private static Log logger = LogFactory.getLog(MovingAverage.class);
    @Override
    public Observable<Tuple> process(Observable<Tuple> inputStream) {
        return inputStream.map(tuple -> {
            logger.info("Got data = " + tuple.toString());
            return tuple.getDouble("measurement");
        })
                .buffer(5)
                .map(data -> tuple().of("average", avg(data)));
    }
}

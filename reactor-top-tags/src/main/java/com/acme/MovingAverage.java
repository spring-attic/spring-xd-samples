package com.acme;


import org.springframework.xd.reactor.Processor;
import org.springframework.xd.tuple.Tuple;
import reactor.rx.Stream;

import static com.acme.Math.avg;
import static org.springframework.xd.tuple.TupleBuilder.tuple;

public class MovingAverage implements Processor<Tuple, Tuple> {
    @Override
    public Stream<Tuple> process(Stream<Tuple> inputStream) {

        return inputStream.map(tuple -> {
            //System.out.println("Inside Moving Average Thread Id = " + Thread.currentThread().getId());
            return tuple.getDouble("measurement");
        })
                .buffer(5)
                .map(data -> tuple().of("average", avg(data)));
    }
}
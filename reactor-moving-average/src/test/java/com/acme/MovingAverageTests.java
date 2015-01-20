package com.acme;

import org.junit.Test;
import org.springframework.xd.reactor.Processor;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;
import reactor.Environment;
import reactor.fn.Consumer;
import reactor.rx.Stream;
import reactor.rx.Streams;
import reactor.rx.action.Broadcaster;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpollack on 12/19/14.
 */
public class MovingAverageTests {

    @Test
    public void simple() {
        Environment.initializeIfEmpty();
        final Broadcaster<Object> broadcaster = Streams.serializedBroadcast();

        Processor processor = new MovingAverage();
        Stream<?> outputStream = processor.process(broadcaster);

        outputStream.consume(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                System.out.println("processed : " + o);
            }
        });

        List<Tuple> inputData = new ArrayList<Tuple>();
        for (int i = 0; i < 10; i++) {
            inputData.add(TupleBuilder.tuple().of("id", i, "measurement", new Double(i+10)));
        }
        for (Tuple tuple: inputData) {
            broadcaster.onNext(tuple);
        }


    }
}

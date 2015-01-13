package com.acme;

import org.junit.Test;
import org.springframework.xd.rxjava.Processor;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpollack on 12/19/14.
 */
public class MovingAverageTests {

    @Test
    public void simple() {
       // Environment.initializeIfEmpty();
       // final Broadcaster<Object> broadcaster = Streams.broadcast();
        final Subject subject = PublishSubject.create();

        Processor processor = new MovingAverage();
        Observable<?> outputStream = processor.process(subject);

        outputStream.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                System.out.println("processed : " + o);
            }
        });

//        outputStream.consume(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) {
//                System.out.println("processed : " + o);
//            }
//        });

        List<Tuple> inputData = new ArrayList<Tuple>();
        for (int i = 0; i < 10; i++) {
            inputData.add(TupleBuilder.tuple().of("id", i, "measurement", new Double(i+10)));
        }
        for (Tuple tuple: inputData) {
            subject.onNext(tuple);
        }


    }
}

package com.acme;

import org.springframework.xd.reactor.Processor;
import reactor.fn.Function;
import reactor.rx.Stream;

public class PongProcessor implements Processor<String, String> {

    @Override
    public Stream<String> process(Stream<String> inputStream) {
        return inputStream.map(new Function<String, String>() {
            @Override
            public String apply(String message) {
                return message + "-pong";
            }
        });
    }
}
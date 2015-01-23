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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.xd.reactor.Processor;
import reactor.Environment;
import reactor.fn.Consumer;
import reactor.rx.Stream;
import reactor.rx.Streams;
import reactor.rx.action.Broadcaster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mark Pollack
 */
public class TopTagsTests {


    protected Environment env;

    @Before
    public void loadEnv() {
        env = Environment.initializeIfEmpty().assignErrorJournal();
    }

    @After
    public void closeEnv() {
        Environment.terminate();
    }

    @Test
    public void tags() throws IOException {

        final Broadcaster<Object> broadcaster = Streams.serializedBroadcast();

        Processor processor = new TopTags();
        Stream<?> outputStream = processor.process(broadcaster);


        outputStream.consume(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                System.out.println("processed : " + o);
            }
        });

        List<String> sampleData = Arrays.asList(
                "foo,bar", "foo", "foo,baz", "foo", "foo,aaa", "aaa", "bbb", "bbb,foo");

        for (String tag : sampleData) {
            broadcaster.onNext(tag);
            simulateLatency();
        }

    }

    private void simulateLatency(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

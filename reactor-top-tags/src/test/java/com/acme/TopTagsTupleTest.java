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

import org.springframework.core.io.ClassPathResource;
import org.springframework.xd.reactor.Processor;

import reactor.Environment;
import reactor.fn.Consumer;
import reactor.rx.Stream;
import reactor.rx.broadcast.Broadcaster;
import reactor.rx.broadcast.SerializedBroadcaster;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author Mark Pollack
 */
public class TopTagsTupleTest {


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

		final Broadcaster<Object> broadcaster = SerializedBroadcaster.create();

		Processor processor = new TopTags(1, 1, 10);
		Stream<?> outputStream = processor.process(broadcaster);


		outputStream.consume(new Consumer<Object>() {
			@Override
			public void accept(Object o) {
				System.out.println("processed : " + o);
			}
			//TODO - expect
//            processed : {"id":"55786760-7472-065d-8e62-eb83260948a4","timestamp":1422399628134,"hashtag":"AndroidGames","count":1}
//            processed : {"id":"bd99050f-abfa-a239-c09a-f2fe721daafb","timestamp":1422399628182,"hashtag":"Android","count":1}
//            processed : {"id":"10ce993c-fd57-322d-efa1-16f810918187","timestamp":1422399628184,"hashtag":"GameInsight","count":1}
		});

		ClassPathResource resource = new ClassPathResource("tweets.json");
		Scanner scanner = new Scanner(resource.getInputStream());
		while (scanner.hasNext()) {
			String tweet = scanner.nextLine();
			broadcaster.onNext(tweet);
			//simulateLatency();
		}
		//System.in.read();

	}

	private void simulateLatency() {
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

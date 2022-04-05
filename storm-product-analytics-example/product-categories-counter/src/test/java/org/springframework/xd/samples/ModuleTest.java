/*
 * Copyright 2014 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.samples;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author David Turanski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/config/module.xml")
public class ModuleTest {
	@Autowired SubscribableChannel output;
	@Autowired MessageChannel input;
	@Autowired RedisTemplate<String,String> redisTemplate;
	
	@Test
	public void test() {
		
		final AtomicInteger received = new AtomicInteger();
		output.subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				Tuple t = (Tuple)message.getPayload();
				received.getAndIncrement();
			}
		});

		List<Tuple> history = new ArrayList<>();
		history.add(TupleBuilder.tuple().of("product", "8", "category", "Players"));
		history.add(TupleBuilder.tuple().of("product", "8", "category", "Players"));
		history.add(TupleBuilder.tuple().of("product", "8", "category", "TVs"));
		history.add(TupleBuilder.tuple().of("product", "8", "category", "Mounts"));
		history.add(TupleBuilder.tuple().of("product", "0", "category", "Phones"));
		history.add(TupleBuilder.tuple().of("product", "2", "category", "Phones"));
		history.add(TupleBuilder.tuple().of("product", "17", "category", "Phones"));
		history.add(TupleBuilder.tuple().of("product", "21", "category", "Phones"));
		
		input.send(new GenericMessage<List<Tuple>>(history));
		assertEquals(7, received.get());
	}

	@After
	public void cleanUp() {
		Set<String> keys = redisTemplate.keys("prodcnt:*");
		redisTemplate.delete(keys);
	}
}

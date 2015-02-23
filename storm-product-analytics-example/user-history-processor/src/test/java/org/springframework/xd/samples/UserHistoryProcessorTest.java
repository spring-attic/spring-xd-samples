/*
 * Copyright 2014 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.samples;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author David Turanski
 */
public class UserHistoryProcessorTest {
	private RedisTemplate<String, String> redisTemplate;

	private UserHistoryProcessor userHistoryProcessor;

	@Before
	public void setUp() {
		JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
		redisConnectionFactory.afterPropertiesSet();
		redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
		userHistoryProcessor = new UserHistoryProcessor(redisTemplate);
	}

	@Test
	public void testUpdateUserHistory() {
		String content = "{\"user\":\"John\",\"product\":\"8\",\"type\":\"PRODUCT\", \"category\":\"Phones\"}";
		Tuple event = TupleBuilder.fromString(content);
		Map<String, Set<String>> items = new HashMap<>();
		Set<String> views = new HashSet<>();
//		User # Category
//		John 0 Players
//		John 2 Players
//		John 17 TVs
//		John 21 Mounts
		views.add("0:Players");
		views.add("2:Players");
		views.add("17:TVs");
		views.add("21:Mounts");
		items.put("John", views);


		userHistoryProcessor.setUserNavigatedItems(items);
		List<Tuple> results = userHistoryProcessor.updateUserHistory(event);

//		# Category
//		8 Players
//		8 Players
//		8 TVs
//		8 Mounts
//		0 Phones
//		2 Phones
//		17 Phones
//		21 Phones

		assertThat(results.size(), is(8));
		int count8 = 0;
		int countPhones = 0;
		for (Tuple t : results) {
			int val;
			val = t.getInt("product");
			if (val == 8) {
				assertThat(t.getString("category"), anyOf(is("Players"), is("TVs"), is("Mounts")));
				count8++;
			}
			else {
				for (int product : new int[] {0, 2, 17, 21}) {
					if (val == product) {
						assertThat(t.getString("category"), is("Phones"));
						countPhones++;
						break;
					}
				}
			}
		}
		assertThat(count8, is(4));
		assertThat(countPhones, is(4));
		
		Set<String> history = redisTemplate.boundSetOps("history:John").members();
		
		assertThat(history.size(), is(1));
		assertThat(history.iterator().next(), is("8:Phones"));
		
	}

	@After
	public void cleanUp() {
		redisTemplate.delete("history:John");
	}

}

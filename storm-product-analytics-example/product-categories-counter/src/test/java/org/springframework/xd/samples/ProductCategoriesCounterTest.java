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


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author David Turanski
 */
public class ProductCategoriesCounterTest {
	private ProductCategoriesCounter productCategoriesCounter;

	private RedisTemplate<String, String> redisTemplate;

	@Before
	public void setUp() {
		JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
		redisConnectionFactory.afterPropertiesSet();

		redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
		productCategoriesCounter = new ProductCategoriesCounter(redisTemplate);
	}


	@Test
	public void test() {
//		# Category
//		8 Players
//		8 Players
//		8 TVs
//		8 Mounts
//		0 Phones
//		2 Phones
//		17 Phones
//		21 Phones
		List<Tuple> history = new ArrayList<>();
		history.add(TupleBuilder.tuple().of("product", "8", "category", "Players"));
		history.add(TupleBuilder.tuple().of("product", "8", "category", "Players"));
		history.add(TupleBuilder.tuple().of("product", "8", "category", "TVs"));
		history.add(TupleBuilder.tuple().of("product", "8", "category", "Mounts"));
		history.add(TupleBuilder.tuple().of("product", "0", "category", "Phones"));
		history.add(TupleBuilder.tuple().of("product", "2", "category", "Phones"));
		history.add(TupleBuilder.tuple().of("product", "17", "category", "Phones"));
		history.add(TupleBuilder.tuple().of("product", "21", "category", "Phones"));

		List<Tuple> results = (List<Tuple>) productCategoriesCounter.splitMessage(new GenericMessage<List<Tuple>>
				(history));

		assertThat(results.size(), is(7));
		for (Tuple t : results) {
			if ("Players".equals(t.getString("category"))) {
				assertThat(t.getInt("count"), is(2));
			}
			else {
				assertThat(t.getInt("count"), is(1));
			}
		}

		Map<Object, Object> entries;
		entries = redisTemplate.boundHashOps("prodcnt:8").entries();
		assertEquals(3, entries.size());
		assertEquals("2", entries.get("Players"));
		assertEquals("1", entries.get("TVs"));
		assertEquals("1", entries.get("Mounts"));

		entries = redisTemplate.boundHashOps("prodcnt:0").entries();
		assertEquals(1, entries.size());
		assertEquals("1", entries.get("Phones"));

		entries = redisTemplate.boundHashOps("prodcnt:2").entries();
		assertEquals(1, entries.size());
		assertEquals("1", entries.get("Phones"));

		entries = redisTemplate.boundHashOps("prodcnt:17").entries();
		assertEquals(1, entries.size());
		assertEquals("1", entries.get("Phones"));

		entries = redisTemplate.boundHashOps("prodcnt:21").entries();
		assertEquals(1, entries.size());
		assertEquals("1", entries.get("Phones"));
	}

	@After
	public void cleanUp() {
		Set<String> keys = redisTemplate.keys("prodcnt:*");
		redisTemplate.delete(keys);
	}
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * @author David Turanski
 */
public class ProductCategoryEnricherTest {
	private RedisTemplate<String,String> redisTemplate;
	private ProductCategoryEnricher productCategoryEnricher;

	@Before
	public void setUp() {
		JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
		redisConnectionFactory.afterPropertiesSet();

		redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
		productCategoryEnricher = new ProductCategoryEnricher(redisTemplate);
		
	}
	
	@Test
	public void testEnrichProductWithCategory() {
		String event = "{\"user\":\"60571253-8315-40ff-8142-f8d68f9d35f0\",\"product\":\"15\",\"type\":\"PRODUCT\"}";
		Tuple enrichedEvent = productCategoryEnricher.enrichEvent(TupleBuilder.fromString(event));
		assertEquals("Covers", enrichedEvent.getString("category"));
	}
}

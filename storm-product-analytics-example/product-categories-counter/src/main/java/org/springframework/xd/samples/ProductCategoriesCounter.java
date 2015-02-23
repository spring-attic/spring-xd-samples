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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * This variation of the ProductCategoriesCounterBolt does not perform any caching or background processing
 *
 * @author David Turanski
 */
public class ProductCategoriesCounter extends AbstractMessageSplitter {

	private final RedisTemplate<String, String> redisTemplate;

	public ProductCategoriesCounter(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}


	public int getProductCategoryCount(String category, String product) {
		int count = 0;
		String sCount = (String) redisTemplate.boundHashOps(buildRedisKey(product)).get(category);
		if (!(sCount == null || "nil".equals(sCount))) {
			count = Integer.valueOf(sCount);
		}
		return count;
	}

	private void storeProductCategoryCount(String category, String product, int count) {
		String key = buildLocalKey(category, product);
		redisTemplate.boundHashOps(buildRedisKey(product)).put(category, String.valueOf(count));
	}

	private String buildRedisKey(String product) {
		return "prodcnt:" + product;
	}

	private String buildLocalKey(String category, String product) {
		return product + ":" + category;
	}

	private int count(String product, String category) {
		int count = getProductCategoryCount(category, product);
		count++;
		storeProductCategoryCount(category, product, count);
		return count;
	}

	@Override
	protected Object splitMessage(Message<?> message) {
		List<Tuple> output = new ArrayList<Tuple>();
		List<Tuple> input = (List<Tuple>) message.getPayload();
		
		Map<String,Tuple> processed = new HashMap<>();
		
		for (Tuple t : input) {
			String product = t.getString("product");
			String category = t.getString("category");
			int count = count(product, category);
			//Conflate to only retain the last count
			Tuple previous = processed.get(buildLocalKey(category, product));
			if (previous != null) {
				output.remove(previous);
			}
			Tuple tcount = TupleBuilder.tuple().of("count", count, "product", product, "category", category);
			output.add(tcount);
			processed.put(buildLocalKey(category,product),tcount);
		}
		return output;
	}
}

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * Modified from Storm example
 *
 * @author David Turanski
 */
public class UserHistoryProcessor {
	private final RedisTemplate<String, String> redisTemplate;

	private Map<String, Set<String>> usersNavigatedItems = new HashMap<String, Set<String>>();

	public UserHistoryProcessor(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	void setUserNavigatedItems(Map<String, Set<String>> usersNavigatedItems) {
		this.usersNavigatedItems = usersNavigatedItems;
	}

	public List<Tuple> updateUserHistory(Tuple input) {
		List<Tuple> results = new ArrayList<>();
		String user = input.getString("user");
		String prod1 = input.getString("product");
		String cat1 = input.getString("category");

		// Product key will have category information embedded.
		String prodKey = prod1 + ":" + cat1;

		Set<String> productsNavigated = getUserNavigationHistory(user);

		// If the user previously navigated this item -> ignore it
		if (!productsNavigated.contains(prodKey)) {

			// Otherwise update related items
			for (String other : productsNavigated) {
				String[] ot = other.split(":");
				String prod2 = ot[0];
				String cat2 = ot[1];
				results.add(TupleBuilder.tuple().of("product", prod1, "category", cat2));
				results.add(TupleBuilder.tuple().of("product", prod2, "category",cat1));
			}
			addProductToHistory(user, prodKey);
		}

		return results;
	}

	private void addProductToHistory(String user, String product) {
		Set<String> userHistory = getUserNavigationHistory(user);
		userHistory.add(product);
		redisTemplate.boundSetOps(buildKey(user)).add(product);
	}

	private Set<String> getUserNavigationHistory(String user) {
		Set<String> userHistory = usersNavigatedItems.get(user);
		if (userHistory == null) {
			userHistory = redisTemplate.boundSetOps(buildKey(user)).members();
			if (userHistory == null) {
				userHistory = new HashSet<String>();
			}
			usersNavigatedItems.put(user, userHistory);
		}
		return userHistory;
	}

	private String buildKey(String user) {
		return "history:" + user;
	}
}

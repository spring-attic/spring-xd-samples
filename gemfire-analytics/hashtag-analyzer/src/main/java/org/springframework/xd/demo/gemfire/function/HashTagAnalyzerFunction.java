/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.xd.demo.gemfire.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.data.gemfire.function.annotation.GemfireFunction;
import org.springframework.data.gemfire.function.annotation.RegionData;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.xd.demo.gemfire.TweetSummary;

/**
 * GemFire Function Implementations
 
 * @author David Turanski
 *
 */
@Component
public class HashTagAnalyzerFunction {
	private boolean ignoreCase = true;
	/**
	* Aggregates counts and sorts by descending count for associated hash tags present with a target hashTag
	*/
	@GemfireFunction
	public Map<String, Integer> aggregateAssociatedHashTags(@RegionData Map<?, ?> data, String targetHashTag) {
		Map<String, Integer> hashTagCounts = new HashMap<String, Integer>();
		ValueComparator vc = new ValueComparator(hashTagCounts);
		TreeMap<String, Integer> sorted = new TreeMap<String, Integer>(vc);

		for (Object obj : data.values()) {
			TweetSummary entry = (TweetSummary) obj;
			List<String> associatedHashTags = getAssociatedHashTags(entry, targetHashTag);
			for (String hashTag : associatedHashTags) {
				if (ignoreCase) {
					hashTag = hashTag.toLowerCase();
				}
				Integer count = hashTagCounts.get(hashTag);
				if (count == null) {
					hashTagCounts.put(hashTag, new Integer(0));
				}
				count = hashTagCounts.get(hashTag) + 1;

				hashTagCounts.put(hashTag, count);
			}
		}

		for (Entry<String, Integer> count : hashTagCounts.entrySet()) {
			int i = count.getValue().intValue();
			sorted.put(count.getKey(), i);
		}

		return sorted;

	}

	/**
	* Aggregates all hashtag counts and sorts by descending count
	*/
	@GemfireFunction
	public Map<String, Integer> getHashTagCounts(@RegionData Map<?, ?> data) {
		Map<String, Integer> hashTagCounts = new HashMap<String, Integer>();

		ValueComparator vc = new ValueComparator(hashTagCounts);

		TreeMap<String, Integer> sorted = new TreeMap<String, Integer>(vc);

		for (Object obj : data.values()) {
			TweetSummary entry = (TweetSummary) obj;
			List<String> hashTags = entry.getHashTags();
			for (String hashTag : hashTags) {
				Integer count = hashTagCounts.get(hashTag);
				if (count == null) {
					hashTagCounts.put(hashTag, new Integer(0));
				}
				count = hashTagCounts.get(hashTag) + 1;
				hashTagCounts.put(hashTag, count);
			}
		}

		for (Entry<String, Integer> count : hashTagCounts.entrySet()) {
			int i = count.getValue().intValue();
			sorted.put(count.getKey(), i);
		}

		return sorted;

	}

	private List<String> getAssociatedHashTags(TweetSummary entry, String targetHashTag) {
		List<String> results = new ArrayList<String>();
		List<String> hashTags = entry.getHashTags();
		if (!CollectionUtils.isEmpty(hashTags)) {
			if (hashTags.contains(targetHashTag) || hashTags.contains(targetHashTag.toUpperCase())
					|| hashTags.contains(targetHashTag.toLowerCase()) || hashTags.contains(proper(targetHashTag))) {
				for (String hashTag : hashTags) {
					if (!hashTag.equalsIgnoreCase(targetHashTag)) {
						results.add(hashTag);
					}
				}
			}
		}
		return results;
	}

	/**
	 * @param targetHashTag
	 * @return
	 */
	private String proper(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	/**
	 * @param ignoreCase the ignoreCase to set
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	@SuppressWarnings("serial")
	static class ValueComparator implements Comparator<String>, Serializable {

		Map<String, Integer> base;

		ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		@Override
		public int compare(String a, String b) {
			if (base.get(a) <= base.get(b)) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}

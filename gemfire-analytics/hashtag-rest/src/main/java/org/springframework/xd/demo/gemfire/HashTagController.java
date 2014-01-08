/*
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.xd.demo.gemfire;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.gemstone.gemfire.cache.Region;


/**
 * @author David Turanski
 *
 */
@RestController
public class HashTagController {
	@Resource(name = "hashtags")
	private Region<String,TweetSummary> hashtags;

	private Map<String,HashTagCQ> hashTaqQueries = new ConcurrentHashMap<String,HashTagCQ>();
	private Map<DeferredResult<List<TweetSummary>>,HashTagCQ> watchedTweetRequests = new ConcurrentHashMap<DeferredResult<List<TweetSummary>>,HashTagCQ>();

	@Autowired 
	HashTagAnalyzerExecutor hashTagAnalyzer;
	@RequestMapping("/associatedhashtags/{target}")
	public @ResponseBody  Map<String,Integer> getAssociatedHashTags(@PathVariable("target") String target) {
		return hashTagAnalyzer.aggregateAssociatedHashTags(target);
	}
	
	@RequestMapping("/hashtagcounts")
	public @ResponseBody  Map<String,Integer> getHashTagCounts() {
		return hashTagAnalyzer.getHashTagCounts();
	}
	
	@RequestMapping("/tweetwatch/{target}")
	public @ResponseBody  DeferredResult<List<TweetSummary>> watchHashTag(@PathVariable("target") final String target) {
		final DeferredResult<List<TweetSummary>> deferredResult = new DeferredResult<List<TweetSummary>>(null, Collections.emptyList());
		  // Add deferredResult to a Queue or a Map...
		HashTagCQ hashTagCq=null;
		List<TweetSummary> results = null;
		if (hashTaqQueries.containsKey(target)) {
			hashTagCq = hashTaqQueries.get(target);
			results = hashTagCq.getEventListener().getTweets();
		} 
		else {
			hashTagCq = new HashTagCQ(hashtags);
			try {
				hashTagCq.afterPropertiesSet();
				hashTaqQueries.put(target, hashTagCq);
			} catch (Exception e) {
				e.printStackTrace();
			}
			results = hashTagCq.createCQ(target);
		}

		watchedTweetRequests.put(deferredResult,hashTagCq);

		deferredResult.onCompletion(new Runnable(){
			@Override
			public void run() {
				watchedTweetRequests.remove(deferredResult).getEventListener().reset();
			}
		});
		
		if (!results.isEmpty()) {
			deferredResult.setResult(results);
		}
		return deferredResult;
	}

}

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
package org.springframework.xd.demo.gemfire;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.query.CqAttributes;
import com.gemstone.gemfire.cache.query.CqAttributesFactory;
import com.gemstone.gemfire.cache.query.CqQuery;
import com.gemstone.gemfire.cache.query.CqResults;
import com.gemstone.gemfire.cache.query.QueryService;
import com.gemstone.gemfire.cache.query.Struct;

/**
 * @author David Turanski
 *
 */
@Component
public class HashTagCQ implements InitializingBean, DisposableBean {
	@Resource(name = "hashtags")
	private Region<String,TweetSummary> hashtags;
	private QueryService queryService;
	private CqQuery hashtagTracker;
	final private HashTagEventListener hashTagEventlistener;
	
	public HashTagCQ() {
		this.hashTagEventlistener = new HashTagEventListener();
	}
	
	public HashTagCQ(Region<String,TweetSummary> hashtags) {
		this();
		this.hashtags = hashtags;
	}

	@SuppressWarnings("resource")
	public static void main(String... args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/client-cache.xml");
		HashTagCQ query = context.getBean(HashTagCQ.class);
		List<TweetSummary> initialResults = query.createCQ("java");
		for (TweetSummary tweet: initialResults) {
			System.out.println(tweet.getText() + " " + tweet.getHashTags());
		}
		System.out.print("press <RETURN> to quit");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	public List<TweetSummary> createCQ(String targetHashtag) {
		List<TweetSummary> initialResults = new ArrayList<TweetSummary>();

		// Create CqAttribute using CqAttributeFactory
		CqAttributesFactory cqf = new CqAttributesFactory();

		cqf.addCqListener(hashTagEventlistener);
		CqAttributes cqa = cqf.create();
		// Name of the CQ and its query
		String cqName = targetHashtag + "_hashtagTracker";
		String queryStr = buildQueryString(targetHashtag);
		try {
			hashtagTracker = queryService.newCq(cqName, queryStr, cqa);
			// Execute CQ, getting the optional initial result set
			// Without the initial result set, the call is hashtagTracker.execute();
			CqResults<Struct> sResults = hashtagTracker.executeWithInitialResults();
			
			for (Struct struct: sResults) {
				TweetSummary tweet = (TweetSummary)struct.get("value");
				initialResults.add(tweet);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return initialResults;
	}

	public HashTagEventListener getEventListener() {
		return hashTagEventlistener;
	}

	/**
	 * @param targetHashtag
	 * @return
	 */
	private String buildQueryString(String targetHashtag) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from /hashtags h where (h.text.contains('")
		.append(targetHashtag.toLowerCase())
		.append("') or h.text.contains('")
		.append(targetHashtag).
		 append("') or h.text.contains('")
		.append(targetHashtag.toUpperCase())
		.append("') or h.text .contains('")
		.append(targetHashtag.substring(0, 1).toUpperCase()+ targetHashtag.substring(1).toLowerCase())
		.append("'))");
		return sb.toString();
	}
	

	@Override
	public void afterPropertiesSet() throws Exception {
		 queryService = hashtags.getRegionService().getQueryService();
	}
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		hashtagTracker.close();
	}
}

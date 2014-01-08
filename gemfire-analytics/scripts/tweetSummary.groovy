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
import org.springframework.xd.demo.gemfire.TweetSummary
import groovy.json.JsonSlurper
import org.joda.time.*
import org.joda.time.format.*
import org.springframework.social.twitter.api.Tweet;
/**
 * Script to transform XD twitterstream or twittersearch source data. This handles native twitter JSON as produced by
 * the twitterstream source or org.springframework.social.twitter.api.Tweet as produced by twittersearch. Note that 
 * twittersearch may be configured to also produce JSON, using outputType=application/json, but the JSON rendered from the 
 * Tweeet object is not the same as the native twitter JSON, so this script would need to be modified to handle that case.
 * 
 * @return a TweetSummary instance
 */
if (payload instanceof String) {
	
	JsonSlurper slurper = new JsonSlurper()
	def result = slurper.parseText(payload)
	DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy");
	
	DateTime dt = result.created_at? DateTime.parse(result.created_at,formatter) : new DateTime();

	TweetSummary tweetSummary = new TweetSummary(
		id:result.id,
		text: result.text,
		hashTags:result.entities?.hashtags*.text,
		lang:result.lang,
		createdAt: dt.getMillis()
	)

	tweetSummary.hashTags = tweetSummary.hashTags?:[]
	return tweetSummary
}
// TODO: For some reason the single arg ctor is not working
TweetSummary tweetSummary = new TweetSummary(id: payload.id, createdAt:payload.createdAt.time,
	text:payload.text,lang:payload.languageCode)
payload.entities.hashTags.each {
	tweetSummary.hashTags.add(it.text)
}
tweetSummary

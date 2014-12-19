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

package my.custom.transformer;

/**
 * @author David Turanski
 */

import static org.junit.Assert.assertEquals;
import static org.springframework.xd.dirt.test.process.SingleNodeProcessingChainSupport.*;

import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.xd.dirt.server.SingleNodeApplication;
import org.springframework.xd.dirt.test.SingleNodeIntegrationTestSupport;
import org.springframework.xd.dirt.test.SingletonModuleRegistry;
import org.springframework.xd.dirt.test.process.SingleNodeProcessingChain;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.test.RandomConfigurationSupport;

/**
 * Unit tests a module deployed to an XD single node container.
 */
public class TweetTransformerIntegrationTest {

	private static SingleNodeApplication application;

	private static int RECEIVE_TIMEOUT = 5000;

	private static String moduleName = "tweet-transformer";

	/**
	 * Start the single node container, binding random unused ports, etc. to not conflict with any other instances
	 * running on this host. Configure the ModuleRegistry to include the project module.
	 */
	@BeforeClass
	public static void setUp() {
		RandomConfigurationSupport randomConfigSupport = new RandomConfigurationSupport();
		application = new SingleNodeApplication().run();
		SingleNodeIntegrationTestSupport singleNodeIntegrationTestSupport = new SingleNodeIntegrationTestSupport
				(application);
		singleNodeIntegrationTestSupport.addModuleRegistry(new SingletonModuleRegistry(ModuleType.processor,
				moduleName));

	}

	/**
	 * Each test creates a stream with the module under test, or in general a "chain" of processors. The
	 * SingleNodeProcessingChain is a test fixture that allows the test to send and receive messages to verify each
	 * message is processed as expected.
	 */
	@Test
	public void test() {
		String streamName = "tweetTest";
		String tweet = "{\n" +
				"      \"coordinates\": null,\n" +
				"      \"favorited\": false,\n" +
				"      \"truncated\": false,\n" +
				"      \"created_at\": \"Mon Sep 24 03:35:21 +0000 2012\",\n" +
				"      \"id_str\": \"250075927172759552\",\n" +
				"      \"entities\": {\n" +
				"        \"urls\": [\n" +
				" \n" +
				"        ],\n" +
				"        \"hashtags\": [\n" +
				"          {\n" +
				"            \"text\": \"freebandnames\",\n" +
				"            \"indices\": [\n" +
				"              20,\n" +
				"              34\n" +
				"            ]\n" +
				"          }\n" +
				"        ],\n" +
				"        \"user_mentions\": [\n" +
				" \n" +
				"        ]\n" +
				"      },\n" +
				"      \"in_reply_to_user_id_str\": null,\n" +
				"      \"contributors\": null,\n" +
				"      \"text\": \"Aggressive Ponytail #freebandnames\",\n" +
				"      \"metadata\": {\n" +
				"        \"iso_language_code\": \"en\",\n" +
				"        \"result_type\": \"recent\"\n" +
				"      },\n" +
				"      \"retweet_count\": 0,\n" +
				"      \"in_reply_to_status_id_str\": null,\n" +
				"      \"id\": 250075927172759552,\n" +
				"      \"geo\": null,\n" +
				"      \"retweeted\": false,\n" +
				"      \"in_reply_to_user_id\": null,\n" +
				"      \"place\": null,\n" +
				"      \"user\": {\n" +
				"        \"profile_sidebar_fill_color\": \"DDEEF6\",\n" +
				"        \"profile_sidebar_border_color\": \"C0DEED\",\n" +
				"        \"profile_background_tile\": false,\n" +
				"        \"name\": \"Sean Cummings\",\n" +
				"        \"profile_image_url\": \"http://a0.twimg.com/profile_images/2359746665/1v6zfgqo8g0d3mk7ii5s_normal.jpeg\",\n" +
				"        \"created_at\": \"Mon Apr 26 06:01:55 +0000 2010\",\n" +
				"        \"location\": \"LA, CA\",\n" +
				"        \"follow_request_sent\": null,\n" +
				"        \"profile_link_color\": \"0084B4\",\n" +
				"        \"is_translator\": false,\n" +
				"        \"id_str\": \"137238150\",\n" +
				"        \"entities\": {\n" +
				"          \"url\": {\n" +
				"            \"urls\": [\n" +
				"              {\n" +
				"                \"expanded_url\": null,\n" +
				"                \"url\": \"\",\n" +
				"                \"indices\": [\n" +
				"                  0,\n" +
				"                  0\n" +
				"                ]\n" +
				"              }\n" +
				"            ]\n" +
				"          },\n" +
				"          \"description\": {\n" +
				"            \"urls\": [\n" +
				" \n" +
				"            ]\n" +
				"          }\n" +
				"        },\n" +
				"        \"default_profile\": true,\n" +
				"        \"contributors_enabled\": false,\n" +
				"        \"favourites_count\": 0,\n" +
				"        \"url\": null,\n" +
				"        \"profile_image_url_https\": \"https://si0.twimg" +
				".com/profile_images/2359746665/1v6zfgqo8g0d3mk7ii5s_normal.jpeg\",\n" +
				"        \"utc_offset\": -28800,\n" +
				"        \"id\": 137238150,\n" +
				"        \"profile_use_background_image\": true,\n" +
				"        \"listed_count\": 2,\n" +
				"        \"profile_text_color\": \"333333\",\n" +
				"        \"lang\": \"en\",\n" +
				"        \"followers_count\": 70,\n" +
				"        \"protected\": false,\n" +
				"        \"notifications\": null,\n" +
				"        \"profile_background_image_url_https\": \"https://si0.twimg.com/images/themes/theme1/bg" +
				".png\",\n" +
				"        \"profile_background_color\": \"C0DEED\",\n" +
				"        \"verified\": false,\n" +
				"        \"geo_enabled\": true,\n" +
				"        \"time_zone\": \"Pacific Time (US & Canada)\",\n" +
				"        \"description\": \"Born 330 Live 310\",\n" +
				"        \"default_profile_image\": false,\n" +
				"        \"profile_background_image_url\": \"http://a0.twimg.com/images/themes/theme1/bg.png\",\n" +
				"        \"statuses_count\": 579,\n" +
				"        \"friends_count\": 110,\n" +
				"        \"following\": null,\n" +
				"        \"show_all_inline_media\": false,\n" +
				"        \"screen_name\": \"sean_cummings\"\n" +
				"      },\n" +
				"      \"in_reply_to_screen_name\": null,\n" +
				"      \"source\": \"<a>Twitter for Mac</a>\",\n" +
				"      \"in_reply_to_status_id\": null\n" +
				"    }";


		String processingChainUnderTest = moduleName;

		SingleNodeProcessingChain chain = chain(application, streamName, processingChainUnderTest);

		chain.sendPayload(tweet);

		String result = (String) chain.receivePayload(RECEIVE_TIMEOUT);

		assertEquals("Aggressive Ponytail #freebandnames", result);
		chain.destroy();
	}

}

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

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.xd.demo.gemfire.function.HashTagAnalyzerFunction;

import com.gemstone.gemfire.cache.Region;

/**
 * A Driver to test the HashTagAnalyzer. This requires some set up
 * The XD Gemfire Cache Server is configured and initialized, e.g., run ./install in this project's
 * root directory. And start the XD cache server
 * 
 * >cd $XD_HOME/gemfire
 * >bin/gemfire-server config/twitter-demo.xml
 * 
 * start xd_singlenode
 * Create Deploy the test streams
 * 
 * From the project root directory:
 * >./tweetsetup
 * 
 * If this has already been done, you can start the XD shell and 
 * 
 * >stream undeploy tweets
 * >stream deploy tweets
 * 
 * to repopulate the cache
 * 
 * @author David Turansk
 *
 */
@Component	
public class HashTagAnalyzerDriver {

	@Resource(name = "hashtags")
	Region<?,?> hashtags;

	//@Autowired
	//HashTagAnalyzerExecutor hashTagAnalyzerExecutor;


	/**
	 * @param args
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/client-cache.xml");
		HashTagAnalyzerDriver driver = context.getBean(HashTagAnalyzerDriver.class);
		driver.run();
	}
	
	public void run() {
		for (Object key: hashtags.keySetOnServer()) {
			hashtags.get(key);
		}
		HashTagAnalyzerFunction fn =new HashTagAnalyzerFunction();
		Map<String,Integer> map = fn.aggregateAssociatedHashTags(hashtags, "jobs");
		for (Entry<String,Integer>entry: map.entrySet()) {
		System.out.println(entry.getKey() + "=" + entry.getValue());	
		}
		System.out.println("\n============ raw counts:");
		map = fn.getHashTagCounts(hashtags);
		for (Entry<String,Integer>entry: map.entrySet()) {
		System.out.println(entry.getKey() + "=" + entry.getValue());	
		}
	}

}

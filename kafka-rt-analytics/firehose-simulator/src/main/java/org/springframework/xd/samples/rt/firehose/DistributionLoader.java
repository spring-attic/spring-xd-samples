/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.xd.samples.rt.firehose;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

/**
 * @author David Turanski
 */
public class DistributionLoader implements ApplicationContextAware {
	private final Logger log = LoggerFactory.getLogger(DistributionLoader.class);

	private final String resourcePath;

	private final FirehoseSimulator simulator;

	private ApplicationContext applicationContext;

	public DistributionLoader(FirehoseSimulator simulator, String resourcePath) {
		this.simulator = simulator;
		this.resourcePath = resourcePath;
	}

	@Scheduled(fixedDelay = 5000)
	public void updateDistributions() {
		log.info("updating distributions ...");
		Resource resource = this.applicationContext.getResource(resourcePath);
		if (resource.exists()) {
			log.info("loading firehose properties");
			Properties props = new Properties();
			try {
				props.load(resource.getInputStream());
				if (props.containsKey("sourceDistribution")) {
					log.info("updating sourceDistribution: {}", props.getProperty("sourceDistribution"));
					simulator.setSourceDistribution(parse(props.getProperty("sourceDistribution")));
				}

				if (props.containsKey("sourceDistribution")) {
					log.info("updating eventDistribution: {}", props.getProperty("eventDistribution"));
					simulator.setEventDistribution(parse(props.getProperty("eventDistribution")));
				}
			}
			catch (IOException e) {
				throw new RuntimeException("unable to load properties from " + resource.getFilename());
			}
		}
		else {
			log.warn("resource {} does not exist.", resource.getFilename());
		}
	}

	private int[] parse(String distribution) {
		if (!StringUtils.hasText(distribution)){
			return null;
		}
		String[] tokens = distribution.split(",");
		int[] percentages = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			percentages[i] = Integer.parseInt(tokens[i]);
		}
		return percentages;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}

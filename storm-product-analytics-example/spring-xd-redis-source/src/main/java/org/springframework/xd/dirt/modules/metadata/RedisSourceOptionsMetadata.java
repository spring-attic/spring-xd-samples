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

package org.springframework.xd.dirt.modules.metadata;

import javax.validation.constraints.AssertTrue;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.ProfileNamesProvider;

/**
 * Options metadata for Redis source module.
 *
 * @author David Turanski
 */
@Mixin(RedisConnectionMixin.class)
public class RedisSourceOptionsMetadata implements ProfileNamesProvider {
	private static final String TOPIC_PROFILE = "use-topic";

	private static final String QUEUE_PROFILE = "use-queue";


	private String topics = null;

	private String queue = null;


	public String getTopics() {
		return topics;
	}

	@ModuleOption("name for the topic or comma delimited string")
	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getQueue() {
		return queue;
	}

	@ModuleOption("name for the queue")
	public void setQueue(String queue) {
		this.queue = queue;
	}

	/**
	 * User can't explicitly set mutually exclusive values together.
	 */
	@AssertTrue(message = "the 'topics' and 'queue' options are mutually exclusive")
	public boolean isOptionMutuallyExclusive() {
		boolean optionSpecified = false;
		String[] distinctOptions = {this.topics, this.queue};
		for (String option : distinctOptions) {
			if (optionSpecified == true && option != null) {
				return false;
			}
			if (option != null) {
				optionSpecified = true;
			}
		}
		return true;
	}

	@AssertTrue(message = "one of 'topics', 'queue' must be set explicitly")
	public boolean isOptionRequired() {
		return ((queue != null) || (topics != null));
	}


	@Override
	public String[] profilesToActivate() {
		if (topics != null) {
			return new String[] {TOPIC_PROFILE};
		}
		else if (queue != null) {
			return new String[] {QUEUE_PROFILE};
		}
		return new String[] {};
	}
}

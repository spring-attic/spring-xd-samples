/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acme;


import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.xd.analytics.metrics.core.FieldValueCounterRepository;

/**
 * @author Marius Bogoevici
 */
public class PayloadValueCounterHandler {

	private String name;

	private FieldValueCounterRepository fieldValueCounterRepository;

	public PayloadValueCounterHandler(FieldValueCounterRepository fieldValueCounterRepository, String name) {
		Assert.hasText(name, "must have a name");
		Assert.notNull(fieldValueCounterRepository, "cannot be null");
		this.name = name;
		this.fieldValueCounterRepository = fieldValueCounterRepository;
	}

	@ServiceActivator
	public void handleMessage(Message<?> message) throws Exception {
		if (message.getPayload() != null) {
			fieldValueCounterRepository.increment(name, message.getPayload().toString());
		}
	}
}

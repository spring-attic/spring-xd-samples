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

import static org.springframework.xd.module.options.spi.ModulePlaceholders.XD_STREAM_NAME;

import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.ModulePlaceholders;

/**
 * @author Marius Bogoevici
 */
public class PayloadValueCounterHandlerOptions {

	private String name = ModulePlaceholders.XD_STREAM_NAME;

	public String getName() {
		return name;
	}

	@ModuleOption(value = "the name of the metric to contribute to (will be created if necessary)", defaultValue = XD_STREAM_NAME)
	public void setName(String name) {
		this.name = name;
	}
}

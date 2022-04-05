/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acme.toptags.counter;

import org.springframework.xd.module.options.spi.ModuleOption;

/**
 * @author Marius Bogoevici
 */
public class RollingTagCounterOptions {

	private int timeWindow = 9;

	private int timeShift = 3;

	public int getTimeWindow() {
		return timeWindow;
	}

	@ModuleOption("The length in seconds of the time window over which tags are counted")
	public void setTimeWindow(int timeWindow) {
		this.timeWindow = timeWindow;
	}

	public int getTimeShift() {
		return timeShift;
	}

	@ModuleOption("The frequency in seconds with which tag counts are emitted")
	public void setTimeShift(int timeShift) {
		this.timeShift = timeShift;
	}
}

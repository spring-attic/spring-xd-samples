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
package io.pivotal.demo.smartgrid.frontend.timeseries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Darimont
 */
public class TimeSeriesCollection implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;

	private final List<TimeSeries> timeSeries;

	private final transient List<String> defaultTimeAxis;

	public TimeSeriesCollection(String name) {
		this(name, null);
	}

	public TimeSeriesCollection(String name, List<String> defaultTimeAxis) {

		this.name = name;
		this.defaultTimeAxis = defaultTimeAxis;
		this.timeSeries = new ArrayList<TimeSeries>();
	}

	public void registerTimeSeries(String name, List<String> timeAxis, List<String> values) {
		timeSeries.add(new TimeSeries(name, timeAxis, values));
	}

	public void registerTimeSeries(String name, List<String> values) {
		registerTimeSeries(name, defaultTimeAxis, values);
	}

	public String getName() {
		return name;
	}

	public List<TimeSeries> getTimeSeries() {
		return timeSeries;
	}
}
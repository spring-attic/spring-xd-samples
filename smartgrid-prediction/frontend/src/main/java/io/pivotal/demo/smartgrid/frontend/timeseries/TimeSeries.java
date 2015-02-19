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
package io.pivotal.demo.smartgrid.frontend.timeseries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Darimont
 */
public class TimeSeries implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private List<DataPoint> data;

	public TimeSeries(String name, List<DataPoint> dataPoints) {

		this.name = name;
		this.data = dataPoints;
	}

	public TimeSeries(String name, List<String> timeAxis, List<String> values) {

		this.name = name;
		this.data = buildDataPoints(timeAxis, values);
	}

	private List<DataPoint> buildDataPoints(List<String> timeAxis, List<String> values) {

		List<DataPoint> points = new ArrayList<DataPoint>();


		for (int i = 0, len = Math.min(values.size(), timeAxis.size()); i < len; i++) {
			points.add(new DataPoint(Long.parseLong(timeAxis.get(i)), Double.parseDouble(values.get(i))));
		}

		return points;
	}

	public String getName() {
		return name;
	}

	public List<DataPoint> getData() {
		return data;
	}
}

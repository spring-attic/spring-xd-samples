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

import java.util.Map;

import io.pivotal.demo.smartgrid.frontend.TimeSeriesDataRequest;

/**
 * @author Thomas Darimont
 */
public interface TimeSeriesRepository {

	int GRID_HOUSE_ID = -1;

	int HOUSE_ID_MIN = 0;
	int HOUSE_ID_MAX = 39;


	String DATE_RANGE_DATETIME_MIN = "2013-09-01T00:00:00.000Z";
	String DATA_RANGE_DATETIME_MAX = "2013-09-02T00:00:00.000Z";

	Map<String,TimeSeriesCollection> getTimeSeriesData(TimeSeriesDataRequest dataRequest);
}

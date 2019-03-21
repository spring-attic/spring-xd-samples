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
package io.pivotal.demo.smartgrid.frontend;

import io.pivotal.demo.smartgrid.frontend.timeseries.DataPointResolution;

public class TimeSeriesDataRequest {

	private int houseId;
	private String fromDateTime;
	private String toDateTime;
	private DataPointResolution resolution;

	public TimeSeriesDataRequest(){}

	public TimeSeriesDataRequest(TimeSeriesDataRequest dataRequest, int houseId){

		this.houseId = houseId;
		this.fromDateTime = dataRequest.getFromDateTime();
		this.toDateTime = dataRequest.getToDateTime();
		this.resolution = dataRequest.getResolution();
	}

	public int getHouseId() {
		return houseId;
	}

	public void setHouseId(int houseId) {
		this.houseId = houseId;
	}

	public String getFromDateTime() {
		return fromDateTime;
	}

	public void setFromDateTime(String fromDateTime) {
		this.fromDateTime = fromDateTime;
	}

	public String getToDateTime() {
		return toDateTime;
	}

	public void setToDateTime(String toDateTime) {
		this.toDateTime = toDateTime;
	}

	public DataPointResolution getResolution() {
		return resolution;
	}

	public void setResolution(DataPointResolution resolution) {
		this.resolution = resolution;
	}

	@Override
	public String toString() {
		return "TimeSeriesDataRequest{" +
				"houseId=" + houseId +
				", fromDateTime='" + fromDateTime + '\'' +
				", toDateTime='" + toDateTime + '\'' +
				", resolution=" + resolution +
				'}';
	}
}
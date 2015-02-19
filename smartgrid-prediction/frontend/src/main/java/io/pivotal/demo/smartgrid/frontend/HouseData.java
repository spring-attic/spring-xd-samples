package io.pivotal.demo.smartgrid.frontend;

import io.pivotal.demo.smartgrid.frontend.timeseries.TimeSeriesCollection;

/**
 * @author Thomas Darimont
 */
public class HouseData {

	private final String houseId;
	private final TimeSeriesCollection timeSeriesData;

	public HouseData(String houseId, TimeSeriesCollection timeSeriesData) {
		this.houseId = houseId;
		this.timeSeriesData = timeSeriesData;
	}

	public String getHouseId() {
		return houseId;
	}

	public TimeSeriesCollection getTimeSeriesData() {
		return timeSeriesData;
	}
}

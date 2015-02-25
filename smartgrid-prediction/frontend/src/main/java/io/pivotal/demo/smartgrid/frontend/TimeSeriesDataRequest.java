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
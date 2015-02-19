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

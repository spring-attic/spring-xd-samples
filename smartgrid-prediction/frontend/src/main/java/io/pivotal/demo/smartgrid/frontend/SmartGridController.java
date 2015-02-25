package io.pivotal.demo.smartgrid.frontend;

import java.util.Map;

import io.pivotal.demo.smartgrid.frontend.timeseries.DataPointResolution;
import io.pivotal.demo.smartgrid.frontend.timeseries.TimeSeriesCollection;
import io.pivotal.demo.smartgrid.frontend.timeseries.TimeSeriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Thomas Darimont
 */
@RestController
public class SmartGridController {

	private static final Logger LOG = LoggerFactory.getLogger(SmartGridController.class);

	private final TimeSeriesRepository timeSeriesRepository;

	@Autowired
	public SmartGridController(TimeSeriesRepository timeSeriesRepository) {
		this.timeSeriesRepository = timeSeriesRepository;
	}

	@RequestMapping("/data")
	public Map<String, TimeSeriesCollection> getDataSet(
			@RequestParam(value = "houseId", defaultValue ="-1") int houseId,
			@RequestParam(value = "from",defaultValue = "2013-09-01T00:00:00.000Z") String fromDateTime,
			@RequestParam(value = "to",defaultValue = "2013-09-02T00:00:00.000Z") String toDateTime,
			@RequestParam(value = "resolution",defaultValue = "MINUTE") DataPointResolution resolution
			) {

		TimeSeriesDataRequest dataRequest = new TimeSeriesDataRequest();
		dataRequest.setHouseId(houseId);
		dataRequest.setFromDateTime(fromDateTime);
		dataRequest.setToDateTime(toDateTime);
		dataRequest.setResolution(resolution);

		LOG.info("Received request {}", dataRequest);

		Map<String, TimeSeriesCollection> timeSeriesData = timeSeriesRepository.getTimeSeriesData(dataRequest);

		LOG.info("Returning data {} -> Records: {}", dataRequest, timeSeriesData.size());

		return timeSeriesData;
	}

	@RequestMapping("/dump")
	public String dump() {

		Map<String, TimeSeriesCollection> dataSet = getDataSet(TimeSeriesRepository.GRID_HOUSE_ID, TimeSeriesRepository.DATE_RANGE_DATETIME_MIN, TimeSeriesRepository.DATA_RANGE_DATETIME_MAX, DataPointResolution.MINUTE);
		LOG.info("DataSet: " + dataSet);

		return "ok";
	}
}

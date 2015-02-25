package io.pivotal.demo.smartgrid.frontend.timeseries;

import io.pivotal.demo.smartgrid.frontend.TimeSeriesDataRequest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Thomas Darimont
 */
@Component
public class AggregateCounterTimeSeriesRepository implements TimeSeriesRepository {

	private static final Logger LOG = LoggerFactory.getLogger(AggregateCounterTimeSeriesRepository.class);

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${xdServerBaseUrl}") private String xdServerBaseUrl;

	@Value("${aggregateCounterUrlPattern}") private String aggregateCounterUrlPattern;

	@PostConstruct
	public void init() {
		pingXdServer();
	}

	private void pingXdServer() {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(xdServerBaseUrl).openConnection();
			con.setRequestMethod("HEAD");

			int timeout = 2000;

			con.setReadTimeout(timeout);
			con.setConnectTimeout(timeout);

			int responseCode = con.getResponseCode();
			
			if (responseCode != HttpURLConnection.HTTP_OK) {
				LOG.error("Bad response from server: {} Response: {}", xdServerBaseUrl, responseCode);
			}
		} catch (Exception ex) {
			LOG.error("Could not connect to server: {} Error: {}: {}", xdServerBaseUrl, ex.getClass().getSimpleName(), ex.getMessage());
		}
	}

	@Override
	public Map<String, TimeSeriesCollection> getTimeSeriesData(TimeSeriesDataRequest dataRequest) {

		int houseId = dataRequest.getHouseId();

		IntStream houseNumStream = houseId == GRID_HOUSE_ID ? IntStream.rangeClosed(HOUSE_ID_MIN, HOUSE_ID_MAX) : IntStream
				.of(houseId);

		List<AggregateCounterCollection> aggregateCounterCollections = houseNumStream.parallel()
				.mapToObj(i -> new TimeSeriesDataRequest(dataRequest, i)).map(this::fetchAggregateCounterData)
				.filter(acc -> acc != null && !acc.getAggregateCounters().isEmpty()).collect(Collectors.toList());

		Map<String, TimeSeriesCollection> result = new HashMap<>();
		for (AggregateCounterCollection acc : aggregateCounterCollections) {

			TimeSeriesCollection tsc = convertToTimeSeriesCollection(acc);
			result.put(tsc.getName(), tsc);
		}

		TimeSeriesCollection totalGridTimeSeriesCollection = aggreagteGridTotalTimeSeries(result);

		result.put("h_-1", totalGridTimeSeriesCollection);

		return result;
	}

	private TimeSeriesCollection aggreagteGridTotalTimeSeries(Map<String, TimeSeriesCollection> result) {
		TimeSeriesCollection totalGridTimeSeriesCollection = new TimeSeriesCollection("grid_h-1");

		for (Map.Entry<String, TimeSeriesCollection> entry : result.entrySet()) {

			TimeSeriesCollection timeSeriesCollection = entry.getValue();

			if (totalGridTimeSeriesCollection.getTimeSeries().isEmpty()) {

				for (TimeSeries timeSeries : timeSeriesCollection.getTimeSeries()) {

					TimeSeries newTimeSeries = new TimeSeries("grid" + timeSeries.getName(), new ArrayList<DataPoint>(
							timeSeries.getData()));
					totalGridTimeSeriesCollection.getTimeSeries().add(newTimeSeries);
				}

				continue;
			}

			List<TimeSeries> timeSeriesList = timeSeriesCollection.getTimeSeries();
			for (int timeSeriesIndex = 0, timeSeriesCount = timeSeriesList.size(); timeSeriesIndex < timeSeriesCount; timeSeriesIndex++) {

				TimeSeries timeSeries = timeSeriesList.get(timeSeriesIndex);
				TimeSeries gridTimeSeries = totalGridTimeSeriesCollection.getTimeSeries().get(timeSeriesIndex);

				List<DataPoint> gridDataPoints = gridTimeSeries.getData();
				List<DataPoint> currentDataPoints = timeSeries.getData();

				for (int dataPointIndex = 0, dataPointCount = currentDataPoints.size(); dataPointIndex < dataPointCount; dataPointIndex++) {

					DataPoint currentDataPoint = currentDataPoints.get(dataPointIndex);
					DataPoint gridDataPoint = gridDataPoints.get(dataPointIndex);

					gridDataPoints.set(dataPointIndex, new DataPoint(gridDataPoint.getTs(), gridDataPoint.getValue()
							+ currentDataPoint.getValue()));
				}
			}
		}
		return totalGridTimeSeriesCollection;
	}

	private TimeSeriesCollection convertToTimeSeriesCollection(AggregateCounterCollection acc) {

		TimeSeriesCollection tsc = new TimeSeriesCollection(acc.getName());

		for (Map.Entry<String, AggregateCounter> entry : acc.getAggregateCounters().entrySet()) {

			String timeSeriesName = entry.getKey();
			AggregateCounter aggregateCounter = entry.getValue();

			List<String> timeAxis = new ArrayList<>();
			List<String> valueAxis = new ArrayList<>();

			for (Map.Entry<String, String> dataPoint : aggregateCounter.getCounts().entrySet()) {

				String pit = dataPoint.getKey();
				String value = dataPoint.getValue();

				LocalDateTime ldt = LocalDateTime.parse(pit, DateTimeFormatter.ISO_DATE_TIME);
				timeAxis.add("" + ldt.toEpochSecond(ZoneOffset.UTC));
				valueAxis.add(value);
			}

			tsc.registerTimeSeries(timeSeriesName, timeAxis, valueAxis);
		}

		return tsc;
	}

	private String makeAggregateCounterUrl(TimeSeriesType timeSeriesType, TimeSeriesDataRequest dataRequest) {

		String baseUrl = String.format(aggregateCounterUrlPattern, xdServerBaseUrl, dataRequest.getHouseId(),
				timeSeriesType.name().toLowerCase());

		UriComponentsBuilder ucb = UriComponentsBuilder.fromHttpUrl(baseUrl)
				.queryParam("resolution", dataRequest.getResolution().name().toLowerCase())
				.queryParam("from", dataRequest.getFromDateTime()).queryParam("to", dataRequest.getToDateTime());

		String url = ucb.build().toString();

		return url;
	}

	private AggregateCounterCollection fetchAggregateCounterData(TimeSeriesDataRequest request) {

		AggregateCounterCollection acc = new AggregateCounterCollection(makeHouseKey(request.getHouseId()));

		try {
			AggregateCounter ac = restTemplate.getForObject(makeAggregateCounterUrl(TimeSeriesType.ACTUAL, request),
					AggregateCounter.class);
			if (ac != null) {
				acc.register(ac.getName(), ac);
			}
		} catch (Exception ex) {

			LOG.error("Error retrieving data for request: {}", request);
			return null;
		}

		try {
			AggregateCounter ac = restTemplate.getForObject(makeAggregateCounterUrl(TimeSeriesType.PREDICTED, request),
					AggregateCounter.class);
			if (ac != null) {
				acc.register(ac.getName(), ac);
			}
		} catch (Exception ex) {

			LOG.error("Error retrieving data for request: {}", request);
			return null;
		}

		return acc;
	}

	private String makeHouseKey(int houseId) {
		return "h_" + houseId;
	}
}

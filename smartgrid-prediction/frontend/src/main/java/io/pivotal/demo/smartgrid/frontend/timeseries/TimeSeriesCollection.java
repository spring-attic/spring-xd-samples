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
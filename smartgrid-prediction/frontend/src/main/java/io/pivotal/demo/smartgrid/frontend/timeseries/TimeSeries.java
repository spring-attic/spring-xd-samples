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

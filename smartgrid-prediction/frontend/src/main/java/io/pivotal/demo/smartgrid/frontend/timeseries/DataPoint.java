package io.pivotal.demo.smartgrid.frontend.timeseries;

import java.io.Serializable;

/**
 * @author Thomas Darimont
 */
public class DataPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	private long ts;
	private double value;

	public DataPoint(long ts, double value) {
		this.ts = ts;
		this.value = value;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
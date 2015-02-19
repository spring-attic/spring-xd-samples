package io.pivotal.demo.smartgrid.frontend.timeseries;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Thomas Darimont
 */
public class AggregateCounterCollection {

	private final String name;
	private final Map<String, AggregateCounter> aggregateCounters;

	public AggregateCounterCollection(String name) {

		this.name = name;
		this.aggregateCounters = new LinkedHashMap<>();
	}

	public void register(String name, AggregateCounter aggregateCounter) {
		aggregateCounters.put(name, aggregateCounter);
	}

	public String getName() {
		return name;
	}

	public Map<String, AggregateCounter> getAggregateCounters() {
		return aggregateCounters;
	}
}
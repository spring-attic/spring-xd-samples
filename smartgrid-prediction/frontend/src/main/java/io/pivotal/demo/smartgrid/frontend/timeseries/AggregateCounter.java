package io.pivotal.demo.smartgrid.frontend.timeseries;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Darimont
 */
public class AggregateCounter {

	private String name;
	private LinkedHashMap<String, String> counts;
	private List<Map<String, String>> links;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getCounts() {
		return counts;
	}

	public void setCounts(LinkedHashMap<String, String> counts) {
		this.counts = counts;
	}

	public List<Map<String, String>> getLinks() {
		return links;
	}

	public void setLinks(List<Map<String, String>> links) {
		this.links = links;
	}
}
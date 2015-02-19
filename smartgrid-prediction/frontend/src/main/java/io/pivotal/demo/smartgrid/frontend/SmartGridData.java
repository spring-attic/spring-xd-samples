package io.pivotal.demo.smartgrid.frontend;

import java.util.Map;

/**
 * @author Thomas Darimont
 */
public class SmartGridData {

	private final Map<String,HouseData> houses;

	public SmartGridData(Map<String, HouseData> houses) {
		this.houses = houses;
	}

	public Map<String, HouseData> getHouses() {
		return houses;
	}
}

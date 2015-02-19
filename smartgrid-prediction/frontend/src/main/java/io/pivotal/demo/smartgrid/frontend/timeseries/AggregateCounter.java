/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
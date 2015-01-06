/*
 * Copyright 2015 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acme;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.rometools.rome.feed.synd.SyndEntry;

/**
 * @author David Turanski
 */
@JsonFilter("foreignMarkup filter")
public class SyndEntryJsonTransformer {
	private final ObjectMapper mapper;

	/**
	 * Configure ObjectMapper to filter out fields causing serialization problems
	 */
	public SyndEntryJsonTransformer() {
		mapper = new ObjectMapper();
		mapper.addMixInAnnotations(SyndEntry.class,this.getClass());
		FilterProvider filterProvider = new SimpleFilterProvider()
				.addFilter("foreignMarkup filter", SimpleBeanPropertyFilter.serializeAllExcept("foreignMarkup"));
		mapper.setFilters(filterProvider);
	}

	/**
	 * Convert from SyndEntry to JSON string
	 * @param entry the SyndEntry
	 * @return JSON string
	 */
	public String toJson(SyndEntry entry) {
		try {
			return mapper.writer().writeValueAsString(entry);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
}
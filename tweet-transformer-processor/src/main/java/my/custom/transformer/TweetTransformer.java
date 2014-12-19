/*
 * Copyright 2014 the original author or authors.
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
package my.custom.transformer;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.transformer.MessageTransformationException;


/**
 * @author David Turanski
 */
@MessageEndpoint
public class TweetTransformer {
	private ObjectMapper mapper = new ObjectMapper();

	@Transformer(inputChannel = "input", outputChannel = "output")
	public String transform(String payload) {
		try {
			Map<String, Object> tweet = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {
			});
			return tweet.get("text").toString();
		}
		catch (IOException e) {
			throw new MessageTransformationException("Unable to transform tweet: " + e.getMessage(), e);
		}
	}
}

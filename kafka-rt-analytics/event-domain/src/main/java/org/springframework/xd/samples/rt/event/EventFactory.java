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
package org.springframework.xd.samples.rt.event;

import java.util.HashMap;
import java.util.Map;



/**
 * @author David Turanski
 */
public abstract class EventFactory {

	public static Event httpRequest(String source, long startTime, long endTime, int statusCode) {
		Map<String, Object> data = new HashMap<>();
		data.put("startTime", startTime);
		data.put("statusCode", statusCode);
		return new AppEvent(source, AppEvent.EventType.httpRequest, endTime, data);
	}

	public static Event login(String source, long timeStamp, String userName, boolean status){
		Map<String, Object> data = new HashMap<>();
		data.put("userName", userName);
		data.put("status", status);
		return new AppEvent(source, AppEvent.EventType.login, timeStamp, data);
	}
	
	public static Event logMessage(String source, long timeStamp, String message, String level) {
		Map<String, Object> data = new HashMap<>();
		data.put("message", message);
		data.put("level",level);
		return new AppEvent(source, AppEvent.EventType.logMessage, timeStamp, data);

	}
}
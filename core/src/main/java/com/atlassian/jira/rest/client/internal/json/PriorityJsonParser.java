/*
 * Copyright (C) 2010 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;

public class PriorityJsonParser implements JsonObjectParser<Priority> {
	private final BasicPriorityJsonParser basicPriorityJsonParser = new BasicPriorityJsonParser();

	@Override
	public Priority parse(JsonObject json) throws JsonParseException {
		final BasicPriority basicPriority = basicPriorityJsonParser.parse(json);
		final String statusColor = json.get("statusColor").getAsString();
		final String description = json.get("description").getAsString();
		final URI iconUri = JsonParseUtil.parseURI(json.get("iconUrl").getAsString());
		return new Priority(basicPriority.getSelf(), basicPriority.getId(), basicPriority
				.getName(), statusColor, description, iconUri);
	}
}

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

import com.atlassian.jira.rest.client.api.domain.IssueLinkType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class IssueLinkTypeJsonParser implements JsonObjectParser<IssueLinkType> {
	private static final String KEY_DIRECTION = "direction";

	@Override
	public IssueLinkType parse(JsonObject json) throws JsonParseException {
		final String name = json.get("name").getAsString();
		final String description = json.get("description").getAsString();
		final String dirStr = json.get(KEY_DIRECTION).getAsString();
		final IssueLinkType.Direction direction;
		if ("OUTBOUND".equals(dirStr)) {
			direction = IssueLinkType.Direction.OUTBOUND;
		} else if ("INBOUND".equals(dirStr)) {
			direction = IssueLinkType.Direction.INBOUND;
		} else {
			throw new JsonParseException("Invalid value of " + KEY_DIRECTION + " key: [" + dirStr + "]");
		}
		return new IssueLinkType(name, description, direction);
	}
}


/*
 * Copyright (C) 2012 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.CustomFieldOption;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;
import java.util.Collections;

/**
 * JSON parser for CustomFieldOption
 *
 * @since v1.0
 */
public class CustomFieldOptionJsonParser implements JsonElementParser<CustomFieldOption> {

	private final JsonArrayParser<Iterable<CustomFieldOption>> childrenParser = GenericJsonArrayParser.create(this);

	@Override
	public CustomFieldOption parse(JsonElement jsonElement) throws JsonParseException {
		final JsonObject json = jsonElement.getAsJsonObject();

		final URI selfUri = JsonParseUtil.getSelfUri(json);
		final long id = json.get("id").getAsLong();
		final String value = JsonParseUtil.getAsString(json, "value");

		final JsonArray childrenArray = json.getAsJsonArray("children");
		final Iterable<CustomFieldOption> children = (childrenArray != null)
				? childrenParser.parse(childrenArray)
				: Collections.<CustomFieldOption>emptyList();

		final JsonObject childObject = json.getAsJsonObject("child");
		final CustomFieldOption child = (childObject != null) ? parse(childObject) : null;

		return new CustomFieldOption(id, selfUri, value, children, child);
	}
}

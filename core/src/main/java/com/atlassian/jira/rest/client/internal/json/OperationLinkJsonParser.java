/*
 * Copyright (C) 2014 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.OperationLink;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class OperationLinkJsonParser implements JsonObjectParser<OperationLink> {
	@Override
	public OperationLink parse(final JsonObject json) throws JsonParseException {
		final String id = JsonParseUtil.getOptionalString(json, "id");
		final String styleClass = JsonParseUtil.getOptionalString(json, "styleClass");
		final String label = json.get("label").getAsString();
		final String title = JsonParseUtil.getOptionalString(json, "title");
		final String href = json.get("href").getAsString();
		final Integer weight = JsonParseUtil.parseOptionInteger(json, "weight");
		final String iconClass = JsonParseUtil.getOptionalString(json, "iconClass");
		return new OperationLink(id, styleClass, label, title, href, weight, iconClass);
	}
}

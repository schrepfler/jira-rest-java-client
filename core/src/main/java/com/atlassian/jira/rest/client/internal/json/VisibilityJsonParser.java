/*
 * Copyright (C) 2011 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import javax.annotation.Nullable;

public class VisibilityJsonParser implements JsonElementParser<Visibility> {
	private static final String ROLE_TYPE = "ROLE";
	private static final String GROUP_TYPE = "GROUP";

	@Override
	public Visibility parse(JsonElement jsonElement) throws JsonParseException {
		final JsonObject json = jsonElement.getAsJsonObject();

		final String type = JsonParseUtil.getAsString(json, "type");
		final Visibility.Type visibilityType;
		if (ROLE_TYPE.equalsIgnoreCase(type)) {
			visibilityType = Visibility.Type.ROLE;
		} else if (GROUP_TYPE.equalsIgnoreCase(type)) {
			visibilityType = Visibility.Type.GROUP;
		} else {
			throw new JsonParseException("[" + type + "] does not represent a valid visibility type. Expected ["
					+ ROLE_TYPE + "] or [" + GROUP_TYPE + "].");
		}
		final String value = JsonParseUtil.getAsString(json, "value");
		return new Visibility(visibilityType, value);
	}

	@Nullable
	public Visibility parseVisibility(JsonObject parentObject) throws JsonParseException {
		if (parentObject.has(CommentJsonParser.VISIBILITY_KEY)) { // JIRA 4.3-rc1 and newer
			return parse(parentObject.get(CommentJsonParser.VISIBILITY_KEY).getAsJsonObject());
		}

		String roleLevel = JsonParseUtil.getOptionalString(parentObject, "roleLevel");
		// in JIRA 4.2 "role" was used instead
		if (roleLevel == null) {
			roleLevel = JsonParseUtil.getOptionalString(parentObject, "role");
		}

		if (roleLevel != null) {
			return Visibility.role(roleLevel);
		}

		final String groupLevel = JsonParseUtil.getOptionalString(parentObject, "groupLevel");
		if (groupLevel != null) {
			return Visibility.group(groupLevel);
		}
		return null;
	}
}

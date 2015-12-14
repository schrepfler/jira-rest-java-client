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

import com.atlassian.jira.rest.client.api.domain.Permission;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PermissionJsonParser implements JsonElementParser<Permission> {
	@Override
	public Permission parse(final JsonElement jsonElement) throws JsonParseException {
		final JsonObject json = jsonElement.getAsJsonObject();

		System.out.println(json);
		final Integer id = JsonParseUtil.getAsInt(json, "id");
		final String key = JsonParseUtil.getAsString(json, "key");
		final String name = JsonParseUtil.getAsString(json, "name");
		final String description = JsonParseUtil.getAsString(json, "description");
		final boolean havePermission = json.get("havePermission").getAsBoolean();
		return new Permission(id, key, name, description, havePermission);
	}
}

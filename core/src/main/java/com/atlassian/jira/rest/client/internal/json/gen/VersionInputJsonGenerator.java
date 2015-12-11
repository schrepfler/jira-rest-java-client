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

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.input.VersionInput;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class VersionInputJsonGenerator implements JsonGenerator<VersionInput> {
	@Override
	public JsonObject generate(VersionInput version) throws JsonParseException {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", version.getName());
		jsonObject.addProperty("project", version.getProjectKey());
		if (version.getDescription() != null) {
			jsonObject.addProperty("description", version.getDescription());
		}
		if (version.getReleaseDate() != null) {
			jsonObject.addProperty("releaseDate", JsonParseUtil.formatDate(version.getReleaseDate()));
		}
		jsonObject.addProperty("released", version.isReleased());
		jsonObject.addProperty("archived", version.isArchived());
		return jsonObject;
	}
}

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

import com.atlassian.jira.rest.client.api.domain.RoleActor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;
import java.net.URISyntaxException;


public class RoleActorJsonParser implements JsonElementParser<RoleActor> {

	private final URI baseJiraUri;

	public RoleActorJsonParser(URI baseJiraUri) {
		this.baseJiraUri = baseJiraUri;
	}

	@Override
	public RoleActor parse(final JsonElement jsonElement) throws JsonParseException {
		final JsonObject json = jsonElement.getAsJsonObject();

		// Workaround for a bug in API. Id field should not be optional, unfortunately it is not returned for an admin role actor.
		final Long id = JsonParseUtil.getOptionalLong(json, "id");
		final String displayName = JsonParseUtil.getAsString(json, "displayName");
		final String type = JsonParseUtil.getAsString(json, "type");
		final String name = JsonParseUtil.getAsString(json, "name");
		return new RoleActor(id, displayName, type, name, parseAvatarUrl(json));
	}

	private URI parseAvatarUrl(final JsonObject json) {
		final String pathToAvatar = JsonParseUtil.getOptionalString(json, "avatarUrl");
		if (pathToAvatar != null) {
			final URI avatarUri;
			try {
				avatarUri = new URI(pathToAvatar);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return null;
			}
			return avatarUri.isAbsolute() ? avatarUri : baseJiraUri.resolve(pathToAvatar);
		} else {
			return null;
		}
	}

}

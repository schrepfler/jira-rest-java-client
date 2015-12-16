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

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.internal.json.CommentJsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class CommentJsonGenerator implements JsonGenerator<Comment> {

	private final ServerInfo serverInfo;

	public CommentJsonGenerator(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	@Override
	public JsonObject generate(Comment comment) throws JsonParseException {
		JsonObject res = new JsonObject();
		if (comment.getBody() != null) {
			res.addProperty("body", comment.getBody());
		}

		final Visibility commentVisibility = comment.getVisibility();
		if (commentVisibility != null) {

			final int buildNumber = serverInfo.getBuildNumber();
			JsonObject visibilityJson = new JsonObject();
			final String commentVisibilityType;
			commentVisibilityType = commentVisibility.getType() == Visibility.Type.GROUP ? "group" : "role";
			visibilityJson.addProperty("type", commentVisibilityType);
			visibilityJson.addProperty("value", commentVisibility.getValue());
			res.add(CommentJsonParser.VISIBILITY_KEY, visibilityJson);
		}

		return res;
	}
}

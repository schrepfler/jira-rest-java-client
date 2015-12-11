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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;

public class CommentJsonParser implements JsonObjectParser<Comment> {

	public static final String VISIBILITY_KEY = "visibility";
	private final VisibilityJsonParser visibilityJsonParser = new VisibilityJsonParser();

	@Override
	public Comment parse(JsonObject json) throws JsonParseException {
		final URI selfUri = JsonParseUtil.getSelfUri(json);
		final Long id = JsonParseUtil.getOptionalLong(json, "id");
		final String body = json.get("body").getAsString();
		final BasicUser author = JsonParseUtil.parseBasicUser(json.get("author").getAsJsonObject());
		final BasicUser updateAuthor = JsonParseUtil.parseBasicUser(json.get("updateAuthor").getAsJsonObject());

		final Visibility visibility = visibilityJsonParser.parseVisibility(json);
		return new Comment(selfUri, body, author, updateAuthor, JsonParseUtil.parseDateTime(json.get("created").getAsString()),
				JsonParseUtil.parseDateTime(json.get("updated").getAsString()), visibility, id);
	}
}

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

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;

import java.net.URI;

public class AttachmentJsonParser implements JsonElementParser<Attachment> {

	private static final String THUMBNAIL = "thumbnail";

	@Override
	public Attachment parse(JsonElement jsonElement) throws JsonParseException {
		final JsonObject json = jsonElement.getAsJsonObject();

		final URI selfUri = JsonParseUtil.getSelfUri(json);
		final String filename = JsonParseUtil.getAsString(json, "filename");
		final BasicUser author = JsonParseUtil.parseBasicUser(json.getAsJsonObject("author"));
		final DateTime creationDate = JsonParseUtil.parseDateTime(json.get("created").getAsString());
		final int size = JsonParseUtil.getAsInt(json, "size");
		final String mimeType = JsonParseUtil.getAsString(json, "mimeType");
		final URI contentURI = JsonParseUtil.parseURI(JsonParseUtil.getAsString(json, "content"));
		final URI thumbnailURI = JsonParseUtil.parseOptionalURI(json, THUMBNAIL);
		return new Attachment(selfUri, filename, author, creationDate, size, mimeType, contentURI, thumbnailURI);
	}
}

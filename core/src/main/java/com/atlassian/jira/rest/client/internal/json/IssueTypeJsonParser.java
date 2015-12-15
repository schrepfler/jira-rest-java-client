/*
 * Copyright (C) 2010-2014 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;

public class IssueTypeJsonParser implements JsonElementParser<IssueType> {
    @Override
    public IssueType parse(JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final URI selfUri = JsonParseUtil.getSelfUri(json);
        final long id = json.get("id").getAsLong();
        final String name = JsonParseUtil.getAsString(json, "name");
        final boolean isSubtask = json.get("subtask").getAsBoolean();
        final String iconUrl = JsonParseUtil.getOptionalString(json, "iconUrl");
        final URI iconUri = iconUrl == null ? null : JsonParseUtil.parseURI(iconUrl);
        final String description = JsonParseUtil.getOptionalString(json, "description");
        return new IssueType(selfUri, id, name, isSubtask, description, iconUri);
    }
}

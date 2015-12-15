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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;

/**
 * JSON parser for Filter.
 *
 * @since v2.0
 */
public class FilterJsonParser implements JsonElementParser<Filter> {

    @Override
    public Filter parse(JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final URI selfUri = JsonParseUtil.getSelfUri(json);
        final long id = json.get("id").getAsLong();
        final String name = JsonParseUtil.getAsString(json, "name");
        final String jql = JsonParseUtil.getAsString(json, "jql");
        final String description = json.has("description")? JsonParseUtil.getAsString(json, "description") : "";
        final URI searchUrl = JsonParseUtil.parseURI(JsonParseUtil.getAsString(json, "searchUrl"));
        final URI viewUrl = JsonParseUtil.parseURI(JsonParseUtil.getAsString(json, "viewUrl"));
        final BasicUser owner = JsonParseUtil.parseBasicUser(json.getAsJsonObject("owner"));
        final boolean favourite = json.get("favourite").getAsBoolean();
        return new Filter(selfUri, id, name, description, jql, viewUrl, searchUrl, owner, favourite);
    }
}

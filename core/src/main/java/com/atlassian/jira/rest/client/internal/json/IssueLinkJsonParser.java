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

import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


import java.net.URI;

public class IssueLinkJsonParser implements JsonElementParser<IssueLink> {
    private final IssueLinkTypeJsonParser issueLinkTypeJsonParser = new IssueLinkTypeJsonParser();

    @Override
    public IssueLink parse(JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final String key = JsonParseUtil.getAsString(json, "issueKey");
        final URI targetIssueUri = JsonParseUtil.parseURI(JsonParseUtil.getAsString(json, "issue"));
        final IssueLinkType issueLinkType = issueLinkTypeJsonParser.parse(json.getAsJsonObject("type"));
        return new IssueLink(key, targetIssueUri, issueLinkType);
    }
}

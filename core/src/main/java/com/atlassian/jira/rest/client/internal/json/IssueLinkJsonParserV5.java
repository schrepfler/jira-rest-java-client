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
import com.atlassian.jira.rest.client.api.domain.IssuelinksType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;

public class IssueLinkJsonParserV5 implements JsonElementParser<IssueLink> {
	private final IssuelinksTypeJsonParserV5 issuelinksTypeJsonParserV5 = new IssuelinksTypeJsonParserV5();

	@Override
	public IssueLink parse(JsonElement jsonElement) throws JsonParseException {
		final JsonObject json = jsonElement.getAsJsonObject();

		final IssuelinksType issuelinksType = issuelinksTypeJsonParserV5.parse(json.getAsJsonObject("type"));
		final IssueLinkType.Direction direction;
		final JsonObject link;
		if (json.has("inwardIssue")) {
			link = json.getAsJsonObject("inwardIssue");
			direction = IssueLinkType.Direction.INBOUND;
		} else {
			link = json.getAsJsonObject("outwardIssue");
			direction = IssueLinkType.Direction.OUTBOUND;
		}

		final String key = JsonParseUtil.getAsString(link, "key");
		final URI targetIssueUri = JsonParseUtil.parseURI(JsonParseUtil.getAsString(link, "self"));
		final IssueLinkType issueLinkType = new IssueLinkType(issuelinksType.getName(),
				direction.equals(IssueLinkType.Direction.INBOUND) ? issuelinksType.getInward()
						: issuelinksType.getOutward(), direction);
		return new IssueLink(key, targetIssueUri, issueLinkType);
	}
}

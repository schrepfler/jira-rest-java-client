/*
 * Copyright (C) 2011 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Collections;

public class SearchResultJsonParser implements JsonObjectParser<SearchResult> {

	@Override
	public SearchResult parse(JsonObject json) throws JsonParseException {
		final int startAt = json.get("startAt").getAsInt();
		final int maxResults = json.get("maxResults").getAsInt();
		final int total = json.get("total").getAsInt();
		final JsonArray issuesJsonArray = json.getAsJsonArray("issues");

		final Iterable<Issue> issues;
		if (issuesJsonArray.size() > 0) {
			final IssueJsonParser issueParser = new IssueJsonParser(json.getAsJsonObject("names"), json.getAsJsonObject("schema"));
			final GenericJsonArrayParser<Issue> issuesParser = GenericJsonArrayParser.create(issueParser);
			issues = issuesParser.parse(issuesJsonArray);
		} else {
			issues = Collections.emptyList();
		}
		return new SearchResult(startAt, maxResults, total, issues);
	}
}

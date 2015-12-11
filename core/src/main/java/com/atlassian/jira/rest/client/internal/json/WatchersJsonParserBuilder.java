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

import com.atlassian.jira.rest.client.api.domain.Watchers;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.BasicWatchers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;
import java.util.Collection;

public class WatchersJsonParserBuilder {

	public static JsonObjectParser<Watchers> createWatchersParser() {
		return new JsonObjectParser<Watchers>() {
			private final BasicUserJsonParser userJsonParser = new BasicUserJsonParser();

			@Override
			public Watchers parse(JsonObject json) throws JsonParseException {
				final Collection<BasicUser> watchers = JsonParseUtil.parseJsonArray(json
						.getAsJsonArray("watchers"), userJsonParser);
				return new Watchers(parseValueImpl(json), watchers);
			}
		};
	}

	public static JsonObjectParser<BasicWatchers> createBasicWatchersParser() {
		return new JsonObjectParser<BasicWatchers>() {
			@Override
			public BasicWatchers parse(JsonObject json) throws JsonParseException {
				return parseValueImpl(json);
			}
		};
	}


	private static BasicWatchers parseValueImpl(JsonObject json) throws JsonParseException {
		final URI self = JsonParseUtil.getSelfUri(json);
		final boolean isWatching = json.get("isWatching").getAsBoolean();
		final int numWatchers = json.get("watchCount").getAsInt();
		return new BasicWatchers(self, isWatching, numWatchers);
	}

}

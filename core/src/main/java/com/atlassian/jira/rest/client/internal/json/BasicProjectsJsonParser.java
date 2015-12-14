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

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;

import java.util.ArrayList;

public class BasicProjectsJsonParser implements JsonArrayParser<Iterable<BasicProject>> {

	private final BasicProjectJsonParser basicProjectJsonParser = new BasicProjectJsonParser();

	@Override
	public Iterable<BasicProject> parse(JsonArray json) throws JsonParseException {
		ArrayList<BasicProject> res = new ArrayList<BasicProject>(json.size());
		for (int i = 0; i < json.size(); i++) {
			res.add(basicProjectJsonParser.parse(json.get(i).getAsJsonObject()));
		}
		return res;
	}
}

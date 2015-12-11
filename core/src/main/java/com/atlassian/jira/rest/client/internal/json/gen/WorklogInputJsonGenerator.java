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

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.joda.time.format.DateTimeFormatter;

public class WorklogInputJsonGenerator implements JsonGenerator<WorklogInput> {

	private final JsonGenerator<Visibility> visibilityGenerator = new VisibilityJsonGenerator();
	private final JsonGenerator<BasicUser> basicUserJsonGenerator = new BasicUserJsonGenerator();
	private final DateTimeFormatter dateTimeFormatter;

	public WorklogInputJsonGenerator() {
		this(JsonParseUtil.JIRA_DATE_TIME_FORMATTER);
	}

	public WorklogInputJsonGenerator(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

	@Override
	public JsonObject generate(final WorklogInput worklogInput) throws JsonParseException {
		JsonObject res = new JsonObject();
		res.addProperty("self", worklogInput.getSelf().toString());
		res.addProperty("comment", worklogInput.getComment());
		res.addProperty("started", dateTimeFormatter.print(worklogInput.getStartDate()));
		res.addProperty("timeSpent", worklogInput.getMinutesSpent() + "m");

		putGeneratedIfNotNull("visibility", worklogInput.getVisibility(), res, visibilityGenerator);
		putGeneratedIfNotNull("author", worklogInput.getAuthor(), res, basicUserJsonGenerator);
		putGeneratedIfNotNull("updateAuthor", worklogInput.getUpdateAuthor(), res, basicUserJsonGenerator);
		return res;
	}

	private <K> JsonObject putGeneratedIfNotNull(final String key, final K value, final JsonObject dest, final JsonGenerator<K> generator)
			throws JsonParseException {
		if (value != null) {
			dest.add(key, generator.generate(value));
		}
		return dest;
	}
}

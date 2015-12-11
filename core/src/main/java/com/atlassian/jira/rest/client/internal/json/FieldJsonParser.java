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

import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.FieldSchema;
import com.atlassian.jira.rest.client.api.domain.FieldType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * JSON parser for JIRA fields.
 */
public class FieldJsonParser implements JsonObjectParser<Field> {

	private final FieldSchemaJsonParser schemaJsonParser = new FieldSchemaJsonParser();

	@Override
	public Field parse(final JsonObject jsonObject) throws JsonParseException {
		final String id = jsonObject.get("id").getAsString();
		final String name = jsonObject.get("name").getAsString();
		final Boolean orderable = jsonObject.get("orderable").getAsBoolean();
		final Boolean navigable = jsonObject.get("navigable").getAsBoolean();
		final Boolean searchable = jsonObject.get("searchable").getAsBoolean();
		final FieldType custom = jsonObject.get("custom").getAsBoolean() ? FieldType.CUSTOM : FieldType.JIRA;
		final FieldSchema schema = jsonObject.has("schema") ? schemaJsonParser.parse(jsonObject.getAsJsonObject("schema")) : null;
		return new Field(id, name, custom, orderable, navigable, searchable, schema);
	}

	public static JsonArrayParser<Iterable<Field>> createFieldsArrayParser() {
		return GenericJsonArrayParser.create(new FieldJsonParser());
	}
}

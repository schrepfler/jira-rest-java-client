package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class IssueUpdateJsonGenerator implements JsonGenerator<Iterable<FieldInput>> {
	private final ComplexIssueInputFieldValueJsonGenerator generator = new ComplexIssueInputFieldValueJsonGenerator();

	@Override
	public JsonObject generate(Iterable<FieldInput> fieldInputs) throws JsonParseException {
		final JsonObject fields = new JsonObject();
		if (fieldInputs != null) {
			for (final FieldInput field : fieldInputs) {
				final JsonElement fieldValue = (field.getValue() == null) ? new JsonNull()
						: generator.generateFieldValueForJson(field.getValue());

				fields.add(field.getId(), fieldValue);
			}
		}
		return fields;
	}
}

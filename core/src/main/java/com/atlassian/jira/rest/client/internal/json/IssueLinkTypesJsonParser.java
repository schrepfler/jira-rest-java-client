package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.IssuelinksType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class IssueLinkTypesJsonParser implements JsonElementParser<Iterable<IssuelinksType>> {
	private final IssuelinksTypeJsonParserV5 issueLinkTypeJsonParser = new IssuelinksTypeJsonParserV5();

	@Override
	public Iterable<IssuelinksType> parse(JsonElement jsonElement) throws JsonParseException {
		final JsonObject json = jsonElement.getAsJsonObject();

		return JsonParseUtil.parseJsonArray(json.getAsJsonArray("issueLinkTypes"), issueLinkTypeJsonParser);
	}
}

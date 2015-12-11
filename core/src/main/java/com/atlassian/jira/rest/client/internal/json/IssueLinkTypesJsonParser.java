package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.IssuelinksType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class IssueLinkTypesJsonParser implements JsonObjectParser<Iterable<IssuelinksType>> {
	private final IssuelinksTypeJsonParserV5 issueLinkTypeJsonParser = new IssuelinksTypeJsonParserV5();

	@Override
	public Iterable<IssuelinksType> parse(JsonObject json) throws JsonParseException {
		return JsonParseUtil.parseJsonArray(json.getAsJsonArray("issueLinkTypes"), issueLinkTypeJsonParser);
	}
}

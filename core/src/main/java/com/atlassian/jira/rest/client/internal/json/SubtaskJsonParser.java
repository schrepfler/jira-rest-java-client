package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;

public class SubtaskJsonParser implements JsonObjectParser<Subtask> {
	private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
	private final StatusJsonParser statusJsonParser = new StatusJsonParser();

	@Override
	public Subtask parse(JsonObject json) throws JsonParseException {
		final URI issueUri = JsonParseUtil.parseURI(json.get("self").getAsString());
		final String issueKey = json.get("key").getAsString();
		final JsonObject fields = json.getAsJsonObject("fields");
		final String summary = fields.get("summary").getAsString();
		final Status status = statusJsonParser.parse(fields.getAsJsonObject("status"));
		final IssueType issueType = issueTypeJsonParser.parse(fields.getAsJsonObject("issuetype"));
		return new Subtask(issueKey, issueUri, summary, issueType, status);
	}
}

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.net.URI;

public class SubtaskJsonParser implements JsonElementParser<Subtask> {
    private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
    private final StatusJsonParser statusJsonParser = new StatusJsonParser();

    @Override
    public Subtask parse(JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final URI issueUri = JsonParseUtil.parseURI(JsonParseUtil.getAsString(json, "self"));
        final String issueKey = JsonParseUtil.getAsString(json, "key");
        final JsonObject fields = json.getAsJsonObject("fields");
        final String summary = JsonParseUtil.getAsString(fields, "summary");
        final Status status = statusJsonParser.parse(fields.getAsJsonObject("status"));
        final IssueType issueType = issueTypeJsonParser.parse(fields.getAsJsonObject("issuetype"));
        return new Subtask(issueKey, issueUri, summary, issueType, status);
    }
}

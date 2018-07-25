package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.IssueTypeScheme;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO: Document this class / interface here
 *
 * @since v7.4
 */
public class IssueTypeSchemeJsonParser implements JsonObjectParser<IssueTypeScheme> {

    @Override
    public IssueTypeScheme parse(JSONObject json) throws JSONException {
        final URI selfUri = JsonParseUtil.getSelfUri(json);
        final Long id = json.getLong("id");
        final String name = json.getString("name");
        final String description = json.getString("description");
        final IssueType defaultIssueType = JsonParseUtil.parseOptionalJsonObject(json, "defaultIssueType", new IssueTypeJsonParser());

        final List<IssueType> issueTypes = JsonParseUtil.parseJsonArray(json.getJSONArray("issueTypes"), new IssueTypeJsonParser());

        return new IssueTypeScheme(selfUri, id, name, description, defaultIssueType, issueTypes);
    }



    public static JsonArrayParser<Iterable<IssueTypeScheme>> createIssueTypeSchemesArrayParser() {
        return GenericJsonArrayParser.create(new IssueTypeSchemeJsonParser());
    }
}

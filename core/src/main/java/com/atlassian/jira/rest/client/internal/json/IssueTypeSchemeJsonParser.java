package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.IssueTypeScheme;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

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
        final String defaultIssueType = json.getString("defaultIssueType");


        return new IssueTypeScheme(selfUri, id, name, description, defaultIssueType);
    }



    public static JsonArrayParser<Iterable<IssueTypeScheme>> createIssueTypeSchemesArrayParser() {
        return GenericJsonArrayParser.create(new IssueTypeSchemeJsonParser());
    }
}

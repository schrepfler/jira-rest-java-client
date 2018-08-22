package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.IssueTypeScheme;
import com.atlassian.jira.rest.client.api.domain.IssueTypeSchemeList;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

/**
 * Handles the parsing of collections of IssueTypeSchemes, which also have a parent-level "expand" property.
 *
 * @since 5.2
 */
public class IssueTypeSchemeListJsonParser implements JsonObjectParser<IssueTypeSchemeList> {

    @Override
    public IssueTypeSchemeList parse(JSONObject json) throws JSONException {

        final String expand = json.getString("expand");

        List<IssueTypeScheme> schemes =
                JsonParseUtil.parseJsonArray(json.getJSONArray("schemes"), new IssueTypeSchemeJsonParser());

        return new IssueTypeSchemeList(expand, schemes);
    }
}

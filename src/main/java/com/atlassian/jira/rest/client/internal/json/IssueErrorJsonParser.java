package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.domain.BulkCreateErrorResult;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Parses
 *
 * @since v1.1
 */
public class IssueErrorJsonParser implements JsonObjectParser<BulkCreateErrorResult> {

	@Override
	public BulkCreateErrorResult parse(final JSONObject json) throws JSONException {
		final Collection<String> errorMessages = new ArrayList<String>();

		final Integer status = json.getInt("status");
		final Integer issueNumber = json.getInt("failedElementNumber");

		final JSONObject errorJsonObject = json.optJSONObject("errorMessage");
		if (errorJsonObject != null) {
			final JSONArray valuesJsonArray = errorJsonObject.toJSONArray(errorJsonObject.names());
			if (valuesJsonArray != null) {
				errorMessages.addAll(JsonParseUtil.toStringCollection(valuesJsonArray));
			}
		}
	   return new BulkCreateErrorResult(status, errorMessages, issueNumber);
	}

}

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @since v2.0
 */
public class AuditChangedValueJsonParser implements JsonObjectParser<AuditChangedValue> {

    @Override
    public AuditChangedValue parse(final JsonObject json) throws JsonParseException {
        final String fieldName = json.get("fieldName").getAsString();
        final String changedFrom = JsonParseUtil.getOptionalString(json, "changedFrom");
        final String changedTo = JsonParseUtil.getOptionalString(json, "changedTo");

        return new AuditChangedValue(fieldName, changedTo, changedFrom);
    }
}

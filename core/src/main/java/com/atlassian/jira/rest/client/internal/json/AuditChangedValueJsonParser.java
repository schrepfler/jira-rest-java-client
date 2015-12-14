package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @since v2.0
 */
public class AuditChangedValueJsonParser implements JsonElementParser<AuditChangedValue> {

    @Override
    public AuditChangedValue parse(final JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final String fieldName = JsonParseUtil.getAsString(json, "fieldName");
        final String changedFrom = JsonParseUtil.getOptionalString(json, "changedFrom");
        final String changedTo = JsonParseUtil.getOptionalString(json, "changedTo");

        return new AuditChangedValue(fieldName, changedTo, changedFrom);
    }
}

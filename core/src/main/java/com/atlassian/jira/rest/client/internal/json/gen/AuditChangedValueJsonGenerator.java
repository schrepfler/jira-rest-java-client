package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @since v2.0
 */
public class AuditChangedValueJsonGenerator implements JsonGenerator<AuditChangedValue> {
    @Override
    public JsonObject generate(AuditChangedValue bean) throws JsonParseException {
        final JsonObject obj = new JsonObject();
        obj.addProperty("fieldName", bean.getFieldName());
        if (bean.getChangedTo() != null) {
            obj.addProperty("changedTo", bean.getChangedTo());
        }
        if (bean.getChangedFrom() != null) {
            obj.addProperty("changedFrom", bean.getChangedFrom());
        }
        return obj;
    }
}

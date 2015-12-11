package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @since v2.0
 */
public class AuditAssociatedItemJsonParser implements JsonObjectParser<AuditAssociatedItem> {

    @Override
    public AuditAssociatedItem parse(final JsonObject json) throws JsonParseException {

        final String id = JsonParseUtil.getOptionalString(json, "id");
        final String name = json.get("name").getAsString();
        final String typeName = json.get("typeName").getAsString();
        final String parentId = JsonParseUtil.getOptionalString(json, "parentId");
        final String parentName = JsonParseUtil.getOptionalString(json, "parentName");

        return new AuditAssociatedItem(id, name, typeName, parentId, parentName);
    }
}

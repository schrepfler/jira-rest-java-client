package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @since v2.0
 */
public class AuditAssociatedItemJsonParser implements JsonElementParser<AuditAssociatedItem> {

    @Override
    public AuditAssociatedItem parse(final JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final String id = JsonParseUtil.getOptionalString(json, "id");
        final String name = JsonParseUtil.getAsString(json, "name");
        final String typeName = JsonParseUtil.getAsString(json, "typeName");
        final String parentId = JsonParseUtil.getOptionalString(json, "parentId");
        final String parentName = JsonParseUtil.getOptionalString(json, "parentName");

        return new AuditAssociatedItem(id, name, typeName, parentId, parentName);
    }
}

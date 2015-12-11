package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 *
 * @since v2.0
 */
public class AuditAssociatedItemJsonGenerator implements JsonGenerator<AuditAssociatedItem> {
    @Override
    public JsonObject generate(AuditAssociatedItem bean) throws JsonParseException {
        JsonObject object = new JsonObject();
        object.addProperty("id", bean.getId());
        object.addProperty("name", bean.getName());
        object.addProperty("typeName", bean.getTypeName());
        object.addProperty("parentId", bean.getParentId());
        object.addProperty("parentName", bean.getParentName());
        return object;
    }
}

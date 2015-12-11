package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import com.atlassian.jira.rest.client.api.domain.AuditRecordInput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import javax.annotation.Nullable;

/**
 * @since v2.0
 */
public class AuditRecordInputJsonGenerator implements JsonGenerator<AuditRecordInput> {
    final AuditAssociatedItemJsonGenerator associatedItemJsonGenerator = new AuditAssociatedItemJsonGenerator();

    @Override
    public JsonObject generate(AuditRecordInput bean) throws JsonParseException {
        JsonObject obj = new JsonObject();

        obj.addProperty("category", bean.getCategory());
        obj.addProperty("summary", bean.getSummary());
        obj.add("objectItem", bean.getObjectItem() != null ? associatedItemJsonGenerator.generate(bean.getObjectItem()) : null);
        obj.add("associatedItems", generateAssociatedItems(bean.getAssociatedItems()));
        obj.add("changedValues", generateChangedValues(bean.getChangedValues()));
        return obj;
    }

    private JsonArray generateChangedValues(@Nullable Iterable<AuditChangedValue> changedValues) throws JsonParseException {
        final AuditChangedValueJsonGenerator generator = new AuditChangedValueJsonGenerator();
        final JsonArray array = new JsonArray();
        if (changedValues != null) {
            for(AuditChangedValue value : changedValues) {
                array.add(generator.generate(value));
            }
        }
        return array;
    }

    protected JsonArray generateAssociatedItems(@Nullable Iterable<AuditAssociatedItem> associatedItems) throws JsonParseException {
        final JsonArray array = new JsonArray();
        if (associatedItems != null) {
            for(AuditAssociatedItem item : associatedItems) {
                array.add(associatedItemJsonGenerator.generate(item));
            }
        }
        return array;
    }
}

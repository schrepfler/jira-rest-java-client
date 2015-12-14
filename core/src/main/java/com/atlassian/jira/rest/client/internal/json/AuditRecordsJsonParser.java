package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import com.atlassian.jira.rest.client.api.domain.AuditRecord;
import com.atlassian.jira.rest.client.api.domain.AuditRecordsData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;

/**
 * @since v2.0
 */
public class AuditRecordsJsonParser implements JsonElementParser<AuditRecordsData> {

    private final AuditAssociatedItemJsonParser associatedItemJsonParser = new AuditAssociatedItemJsonParser();
    private final AuditChangedValueJsonParser changedValueJsonParser = new AuditChangedValueJsonParser();
    private final SingleAuditRecordJsonParser singleAuditRecordJsonParser = new SingleAuditRecordJsonParser();

    @Override
    public AuditRecordsData parse(final JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final Integer offset = JsonParseUtil.getAsInt(json, "offset");
        final Integer limit = JsonParseUtil.getAsInt(json, "limit");
        final Integer total = JsonParseUtil.getAsInt(json, "total");
        final OptionalIterable<AuditRecord> records = JsonParseUtil.parseOptionalJsonArray(json.get("records").getAsJsonArray(), singleAuditRecordJsonParser);

        return new AuditRecordsData(offset, limit, total, records);
    }

    class SingleAuditRecordJsonParser implements JsonElementParser<AuditRecord> {
        @Override
        public AuditRecord parse(final JsonElement jsonElement) throws JsonParseException {
            final JsonObject json = jsonElement.getAsJsonObject();

            final Long id =  json.get("id").getAsLong();
            final String summary = JsonParseUtil.getAsString(json, "summary");

            final String createdString = JsonParseUtil.getAsString(json, "created");
            final DateTime created = JsonParseUtil.parseDateTime(json, "created");
            final String category = JsonParseUtil.getAsString(json, "category");
            final String eventSource = JsonParseUtil.getAsString(json, "eventSource");
            final String authorKey = JsonParseUtil.getOptionalString(json, "authorKey");
            final String remoteAddress = JsonParseUtil.getOptionalString(json, "remoteAddress");
            final AuditAssociatedItem objectItem = JsonParseUtil.getOptionalJsonObject(json, "objectItem", associatedItemJsonParser);
            final OptionalIterable<AuditAssociatedItem> associatedItem = JsonParseUtil.parseOptionalJsonArray(json.getAsJsonArray("associatedItems"), associatedItemJsonParser);
            final OptionalIterable<AuditChangedValue> changedValues = JsonParseUtil.parseOptionalJsonArray(json.getAsJsonArray("changedValues"), changedValueJsonParser);

            return new AuditRecord(id, summary, remoteAddress, created, category, eventSource, authorKey, objectItem, associatedItem, changedValues);
        }

    }
}

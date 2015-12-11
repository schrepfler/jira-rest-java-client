package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import com.atlassian.jira.rest.client.api.domain.AuditRecord;
import com.atlassian.jira.rest.client.api.domain.AuditRecordsData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;

/**
 * @since v2.0
 */
public class AuditRecordsJsonParser implements JsonObjectParser<AuditRecordsData> {

    private final AuditAssociatedItemJsonParser associatedItemJsonParser = new AuditAssociatedItemJsonParser();
    private final AuditChangedValueJsonParser changedValueJsonParser = new AuditChangedValueJsonParser();
    private final SingleAuditRecordJsonParser singleAuditRecordJsonParser = new SingleAuditRecordJsonParser();

    @Override
    public AuditRecordsData parse(final JsonObject json) throws JsonParseException {
        final Integer offset = json.get("offset").getAsInt();
        final Integer limit = json.get("limit").getAsInt();
        final Integer total = json.get("total").getAsInt();
        final OptionalIterable<AuditRecord> records = JsonParseUtil.parseOptionalJsonArray(json.get("records").getAsJsonArray(), singleAuditRecordJsonParser);

        return new AuditRecordsData(offset, limit, total, records);
    }

    class SingleAuditRecordJsonParser implements  JsonObjectParser<AuditRecord> {
        @Override
        public AuditRecord parse(final JsonObject json) throws JsonParseException {
            final Long id =  json.get("id").getAsLong();
            final String summary = json.get("summary").getAsString();

            final String createdString = json.get("created").toString();
            final DateTime created = JsonParseUtil.parseDateTime(json, "created");
            final String category = json.get("category").getAsString();
            final String eventSource = json.get("eventSource").getAsString();
            final String authorKey = JsonParseUtil.getOptionalString(json, "authorKey");
            final String remoteAddress = JsonParseUtil.getOptionalString(json, "remoteAddress");
            final AuditAssociatedItem objectItem = JsonParseUtil.getOptionalJsonObject(json, "objectItem", associatedItemJsonParser);
            final OptionalIterable<AuditAssociatedItem> associatedItem = JsonParseUtil.parseOptionalJsonArray(json.get("associatedItems").getAsJsonArray(), associatedItemJsonParser);
            final OptionalIterable<AuditChangedValue> changedValues = JsonParseUtil.parseOptionalJsonArray(json.get("changedValues").getAsJsonArray(), changedValueJsonParser);

            return new AuditRecord(id, summary, remoteAddress, created, category, eventSource, authorKey, objectItem, associatedItem, changedValues);
        }

    }
}

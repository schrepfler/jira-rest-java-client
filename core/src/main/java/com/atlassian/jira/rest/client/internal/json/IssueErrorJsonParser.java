/*
 * Copyright (C) 2012 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.BulkOperationErrorResult;
import com.atlassian.jira.rest.client.api.domain.util.ErrorCollection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Parses collection of errors returned from bulk issue create operation
 *
 * @since v2.0
 */
public class IssueErrorJsonParser implements JsonElementParser<BulkOperationErrorResult> {

    @Override
    public BulkOperationErrorResult parse(final JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final Integer status = JsonParseUtil.getAsInt(json, "status");
        final Integer issueNumber = JsonParseUtil.getAsInt(json, "failedElementNumber");

        final JsonObject elementErrors = json.getAsJsonObject("elementErrors");
        final JsonObject jsonErrors = elementErrors.getAsJsonObject("errors");
        final JsonArray jsonErrorMessages = elementErrors.getAsJsonArray("errorMessages");

        final Collection<String> errorMessages;
        if (jsonErrorMessages != null) {
            errorMessages = JsonParseUtil.toStringCollection(jsonErrorMessages);
        } else {
            errorMessages = Collections.emptyList();
        }

        final Map<String, String> errors;
        if (jsonErrors != null) {
            errors = JsonParseUtil.toStringMap(jsonErrors);
        } else {
            errors = Collections.emptyMap();
        }

        return new BulkOperationErrorResult(new ErrorCollection(status, errorMessages, errors), issueNumber);
    }

}

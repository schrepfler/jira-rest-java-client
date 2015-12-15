/*
 * Copyright (C) 2012 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.util.Map;

/**
 * Json Generator for ComplexIssueInputFieldValue
 *
 * @since v1.0
 */
public class ComplexIssueInputFieldValueJsonGenerator implements JsonGenerator<ComplexIssueInputFieldValue> {
    @Override
    public JsonObject generate(ComplexIssueInputFieldValue bean) throws JsonParseException {
        final JsonObject json = new JsonObject();
        for (Map.Entry<String, Object> entry : bean.getValuesMap().entrySet()) {
            json.add(entry.getKey(), generateFieldValueForJson(entry.getValue()));
        }
        return json;
    }

    public JsonElement generateFieldValueForJson(Object rawValue) throws JsonParseException {
        if (rawValue == null) {
            return new JsonNull();
        } else if (rawValue instanceof ComplexIssueInputFieldValue) {
            return generate((ComplexIssueInputFieldValue) rawValue);
        } else if (rawValue instanceof Iterable) {
            // array with values
            final JsonArray array = new JsonArray();
            for (Object value : (Iterable) rawValue) {
                array.add(generateFieldValueForJson(value));
            }
            return array;
        } else if (rawValue instanceof CharSequence) {
            return new JsonPrimitive(rawValue.toString());
        } else if (rawValue instanceof Number) {
            return new JsonPrimitive((Number) rawValue);
        } else {
            throw new JsonParseException("Cannot generate value - unknown type for me: " + rawValue.getClass());
        }
    }
}

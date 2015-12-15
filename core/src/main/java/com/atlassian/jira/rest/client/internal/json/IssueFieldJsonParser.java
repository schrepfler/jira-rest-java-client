/*
 * Copyright (C) 2010 Atlassian
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

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.HashMap;
import java.util.Map;

public class IssueFieldJsonParser {
    private static final String VALUE_ATTRIBUTE = "value";

    private Map<String, JsonElementParser> registeredValueParsers = new HashMap<String, JsonElementParser>() {{
        put("com.atlassian.jira.plugin.system.customfieldtypes:float", new FloatingPointFieldValueParser());
        put("com.atlassian.jira.plugin.system.customfieldtypes:userpicker", new FieldValueJsonParser<BasicUser>(new BasicUserJsonParser()));
        put("java.lang.String", new StringFieldValueParser());
    }};

    @SuppressWarnings("unchecked")
    public IssueField parse(JsonObject jsonObject, String id) throws JsonParseException {
        String type = JsonParseUtil.getAsString(jsonObject, "type");
        final String name = JsonParseUtil.getAsString(jsonObject, "name");
        final JsonElement valueObject = jsonObject.get(VALUE_ATTRIBUTE);
        final Object value;
        // @todo ugly hack until https://jdog.atlassian.com/browse/JRADEV-3220 is fixed
        if ("comment".equals(name)) {
            type = "com.atlassian.jira.Comment";
        }

        if (valueObject == null) {
            value = null;
        } else {
            final JsonElementParser valueParser = registeredValueParsers.get(type);
            if (valueParser != null) {
                value = valueParser.parse(jsonObject);
            } else {
                value = valueObject.getAsString();
            }
        }
        return new IssueField(id, name, type, value);
    }

    static class FieldValueJsonParser<T> implements JsonElementParser<T> {
        private final JsonElementParser<T> jsonParser;

        public FieldValueJsonParser(JsonElementParser<T> jsonParser) {
            this.jsonParser = jsonParser;
        }

        @Override
        public T parse(JsonElement jsonElement) throws JsonParseException {
            final JsonObject json = jsonElement.getAsJsonObject();

            final JsonObject valueObject = json.getAsJsonObject(VALUE_ATTRIBUTE);
            if (valueObject == null) {
                throw new JsonParseException("Expected JsonObject with [" + VALUE_ATTRIBUTE + "] attribute present.");
            }
            return jsonParser.parse(valueObject);
        }
    }


    static class FloatingPointFieldValueParser implements JsonElementParser<Double> {

        @Override
        public Double parse(JsonElement jsonElement) throws JsonParseException {
            final JsonObject json = jsonElement.getAsJsonObject();

            final String s = JsonParseUtil.getNullableString(json, VALUE_ATTRIBUTE);
            if (s == null) {
                return null;
            }
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                throw new JsonParseException("[" + s + "] is not a valid floating point number");
            }
        }
    }

    static class StringFieldValueParser implements JsonElementParser<String> {

        @Override
        public String parse(JsonElement jsonElement) throws JsonParseException {
            final JsonObject json = jsonElement.getAsJsonObject();

            return JsonParseUtil.getNullableString(json, VALUE_ATTRIBUTE);
        }
    }


}

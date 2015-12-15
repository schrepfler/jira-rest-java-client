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

import com.atlassian.jira.rest.client.api.domain.Transition;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Collection;
import java.util.Map;

public class TransitionJsonParserV5 implements JsonElementParser<Transition> {
    private final TransitionFieldJsonParser transitionFieldJsonParser = new TransitionFieldJsonParser();

    public Transition parse(JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final int id = JsonParseUtil.getAsInt(json, "id");
        final String name = JsonParseUtil.getAsString(json, "name");
        final JsonObject fieldsObj = json.getAsJsonObject("fields");
        final Collection<Transition.Field> fields = Lists.newArrayList();
        for (Map.Entry<String, JsonElement> entry: fieldsObj.entrySet()) {
            fields.add(transitionFieldJsonParser.parse(entry.getValue().getAsJsonObject(), entry.getKey()));
        }
        return new Transition(name, id, fields);
    }

    public static class TransitionFieldJsonParser {
        public Transition.Field parse(JsonObject json, final String id) throws JsonParseException {
            final boolean isRequired = json.get("required").getAsBoolean();
            final String type = JsonParseUtil.getAsString(json.getAsJsonObject("schema"), "type");
            return new Transition.Field(id, isRequired, type);
        }
    }
}

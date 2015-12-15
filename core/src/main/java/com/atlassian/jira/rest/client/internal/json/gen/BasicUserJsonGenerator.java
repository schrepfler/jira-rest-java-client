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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class BasicUserJsonGenerator implements JsonGenerator<BasicUser> {
    @Override
    public JsonObject generate(BasicUser user) throws JsonParseException {
        JsonObject obj = new JsonObject();
        obj.addProperty("self", user.getSelf().toString());
        obj.addProperty("name", user.getName());
        obj.addProperty("displayName", user.getDisplayName());
        return obj;
    }
}

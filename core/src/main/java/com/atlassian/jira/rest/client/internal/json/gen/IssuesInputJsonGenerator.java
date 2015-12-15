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

import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Json Generator for IssuesInput
 *
 * @since 1.0
 */
public class IssuesInputJsonGenerator implements JsonGenerator<Iterable<IssueInput>> {

    private final IssueInputJsonGenerator issueInputJsonGenerator = new IssueInputJsonGenerator();

    @Override
    public JsonObject generate(final Iterable<IssueInput> issues) throws JsonParseException {
        final JsonObject issueUpdates = new JsonObject();
        final JsonArray obj = new JsonArray();

        for (final IssueInput issue : issues) {
            obj.add(issueInputJsonGenerator.generate(issue));
        }
        issueUpdates.add("issueUpdates", obj);
        return issueUpdates;
    }
}

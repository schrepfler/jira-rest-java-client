/*
 * Copyright (C) 2011 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.AssigneeType;
import com.atlassian.jira.rest.client.internal.domain.AssigneeTypeConstants;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.internal.domain.input.ComponentInputWithProjectKey;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ComponentInputWithProjectKeyJsonGenerator implements JsonGenerator<ComponentInputWithProjectKey> {

    @Override
    public JsonObject generate(ComponentInputWithProjectKey componentInput) throws JsonParseException {
        JsonObject res = new JsonObject();
        if (componentInput.getProjectKey() != null) {
            res.addProperty("project", componentInput.getProjectKey());
        }
        if (componentInput.getName() != null) {
            res.addProperty("name", componentInput.getName());
        }
        if (componentInput.getDescription() != null) {
            res.addProperty("description", componentInput.getDescription());
        }
        if (componentInput.getLeadUsername() != null) {
            res.addProperty("leadUserName", componentInput.getLeadUsername());
        }
        final AssigneeType assigneeType = componentInput.getAssigneeType();
        if (assigneeType != null) {
            final String assigneeTypeStr;
            switch (assigneeType) {
                case PROJECT_DEFAULT:
                    assigneeTypeStr = AssigneeTypeConstants.PROJECT_DEFAULT;
                    break;
                case COMPONENT_LEAD:
                    assigneeTypeStr = AssigneeTypeConstants.COMPONENT_LEAD;
                    break;
                case PROJECT_LEAD:
                    assigneeTypeStr = AssigneeTypeConstants.PROJECT_LEAD;
                    break;
                case UNASSIGNED:
                    assigneeTypeStr = AssigneeTypeConstants.UNASSIGNED;
                    break;
                default:
                    throw new RestClientException("Unexpected assignee type [" + assigneeType + "]", null);
            }
            res.addProperty("assigneeType", assigneeTypeStr);
        }
        return res;
    }
}

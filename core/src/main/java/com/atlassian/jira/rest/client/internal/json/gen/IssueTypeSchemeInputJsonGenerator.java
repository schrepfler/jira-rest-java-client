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

import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueTypeSchemeInput;
import com.atlassian.jira.rest.client.api.domain.input.PropertyInput;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Json Generator for IssueTypeScheme Input
 *
 * @since ??FIXME
 */
public class IssueTypeSchemeInputJsonGenerator implements JsonGenerator<IssueTypeSchemeInput> {


    @Override
    public JSONObject generate(IssueTypeSchemeInput schemeInput) throws JSONException {

        JSONObject res = new JSONObject();
        if (schemeInput.getName() != null) {
            res.put("name", schemeInput.getName());
        }
        if (schemeInput.getDescription() != null) {
            res.put("description", schemeInput.getDescription());
        }
        if (schemeInput.getIssueTypeIds() != null) {
            res.put("issueTypes", schemeInput.getIssueTypeIds());
        }
        if (schemeInput.getDefaultIssueTypeId() != null) {
            res.put("defaultIssueType", schemeInput.getDefaultIssueTypeId());
        }

        return res;
    }
}

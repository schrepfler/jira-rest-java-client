/*
 * Copyright (C) 2011 Atlassian
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

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.internal.ServerVersionConstants;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class LinkIssuesInputGenerator implements JsonGenerator<LinkIssuesInput> {

	private final ServerInfo serverInfo;

	public LinkIssuesInputGenerator(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	@Override
	public JsonObject generate(LinkIssuesInput linkIssuesInput) throws JsonParseException {
		JsonObject res = new JsonObject();

		final int buildNumber = serverInfo.getBuildNumber();
		if (buildNumber >= ServerVersionConstants.BN_JIRA_5) {
			String propertyName = "name";
			if (buildNumber >= ServerVersionConstants.BN_JIRA_7_0) {
				propertyName = "key";
			}
			JsonObject name = new JsonObject();
			name.addProperty("name", linkIssuesInput.getLinkType() );
			res.add("type", name);
			JsonObject inward = new JsonObject();
			inward.addProperty(propertyName, linkIssuesInput.getFromIssueKey() );
			res.add("inwardIssue", inward);
			JsonObject outward = new JsonObject();
			outward.addProperty(propertyName, linkIssuesInput.getToIssueKey() );
			res.add("outwardIssue", outward);
		} else {
			res.addProperty("linkType", linkIssuesInput.getLinkType());
			res.addProperty("fromIssueKey", linkIssuesInput.getFromIssueKey());
			res.addProperty("toIssueKey", linkIssuesInput.getToIssueKey());
		}
		if (linkIssuesInput.getComment() != null) {
			res.add("comment", new CommentJsonGenerator(serverInfo).generate(linkIssuesInput.getComment()));
		}
		return res;
	}
}

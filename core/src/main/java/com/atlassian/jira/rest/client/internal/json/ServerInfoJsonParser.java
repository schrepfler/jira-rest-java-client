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

import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;

import java.net.URI;

public class ServerInfoJsonParser implements JsonObjectParser<ServerInfo> {
	@Override
	public ServerInfo parse(JsonObject json) throws JsonParseException {
		final URI baseUri = JsonParseUtil.parseURI(json.get("baseUrl").getAsString());
		final String version = json.get("version").getAsString();
		final int buildNumber = json.get("buildNumber").getAsInt();
		final DateTime buildDate = JsonParseUtil.parseDateTime(json, "buildDate");
		final DateTime serverTime = JsonParseUtil.parseOptionalDateTime(json, "serverTime");
		final String scmInfo = json.get("scmInfo").getAsString();
		final String serverTitle = json.get("serverTitle").getAsString();
		return new ServerInfo(baseUri, version, buildNumber, buildDate, serverTime, scmInfo, serverTitle);
	}
}

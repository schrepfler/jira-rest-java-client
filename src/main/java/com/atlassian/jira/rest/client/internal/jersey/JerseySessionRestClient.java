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

package com.atlassian.jira.rest.client.internal.jersey;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.SessionRestClient;
import com.atlassian.jira.rest.client.domain.Authentication;
import com.atlassian.jira.rest.client.domain.Session;
import com.atlassian.jira.rest.client.internal.json.AuthenticationJsonParser;
import com.atlassian.jira.rest.client.internal.json.SessionJsonParser;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Jersey-based implementation of SessionRestClient.
 *
 * @since v0.1
 */
public class JerseySessionRestClient extends AbstractJerseyRestClient  implements SessionRestClient {
	private final SessionJsonParser sessionJsonParser = new SessionJsonParser();
    private final AuthenticationJsonParser authenticationJsonParser = new AuthenticationJsonParser();

	public JerseySessionRestClient(ApacheHttpClient client, URI serverUri, boolean followRedirects) {
		super(serverUri, client, followRedirects);
	}

	@Override
	public Session getCurrentSession(ProgressMonitor progressMonitor) {
		return getAndParse(UriBuilder.fromUri(baseUri).path("rest/auth/latest/session").build(), sessionJsonParser, progressMonitor);
	}

    @Override
    public Authentication login(String username, String password, ProgressMonitor progressMonitor) throws RestClientException {
        try {
            return postAndParse(
                UriBuilder.fromUri(baseUri).path("rest/auth/1/session").build(),
                new JSONObject().put("username", username).put("password", password), authenticationJsonParser, progressMonitor);
        } catch (JSONException e) {
            throw new RestClientException(e);
        }
    }
}

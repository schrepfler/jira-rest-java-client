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

import com.atlassian.jira.rest.client.MetadataRestClient;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.internal.json.*;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.Callable;

/**
 * Jersey-based implementation of MetadataRestClient
 *
 * @since v0.1
 */
public class JerseyMetadataRestClient extends AbstractJerseyRestClient implements MetadataRestClient {
	private final String SERVER_INFO_RESOURCE = "/serverInfo";
	private final ServerInfoJsonParser serverInfoJsonParser = new ServerInfoJsonParser();
    private SessionInfoParser sessionInfoJsonParser = new SessionInfoParser();
    private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
    private final GenericJsonArrayParser<IssueType> issueTypesJsonParser = GenericJsonArrayParser.create(issueTypeJsonParser);
    private final StatusJsonParser statusJsonParser = new StatusJsonParser();
    private final GenericJsonArrayParser<Status> statusesJsonParser = GenericJsonArrayParser.create(statusJsonParser);
    private final PriorityJsonParser priorityJsonParser = new PriorityJsonParser();
    private final GenericJsonArrayParser<Priority> prioritiesJsonParser = GenericJsonArrayParser.create(priorityJsonParser);
    private final ResolutionJsonParser resolutionJsonParser = new ResolutionJsonParser();
    private final GenericJsonArrayParser<Resolution> resolutionsJsonParser = GenericJsonArrayParser.create(resolutionJsonParser);
    private final IssueLinkTypesJsonParser issueLinkTypesJsonParser = new IssueLinkTypesJsonParser();

    public JerseyMetadataRestClient(URI baseUri, ApacheHttpClient client, boolean followRedirects) {
		super(baseUri, client, followRedirects);
	}

	@Override
	public IssueType getIssueType(final URI uri, ProgressMonitor progressMonitor) {
		return getAndParse(uri, issueTypeJsonParser, progressMonitor);
	}

	@Override
	public Iterable<IssueType> getIssueTypes(ProgressMonitor progressMonitor) {
		final URI uri = UriBuilder.fromUri(baseUri).path("issuetype").build();
		return getAndParse(uri, issueTypesJsonParser, progressMonitor);
	}

    @Override
	public Iterable<IssuelinksType> getIssueLinkTypes(ProgressMonitor progressMonitor) {
		final URI uri = UriBuilder.fromUri(baseUri).path("issueLinkType").build();
		return getAndParse(uri, issueLinkTypesJsonParser, progressMonitor);
	}

    @Override
    public Iterable<Status> getStatuses(ProgressMonitor progressMonitor) {
        final URI uri = UriBuilder.fromUri(baseUri).path("status").build();
        return getAndParse(uri, statusesJsonParser, progressMonitor);
    }

    @Override
	public Status getStatus(final URI uri, ProgressMonitor progressMonitor) {
		return getAndParse(uri, statusJsonParser, progressMonitor);
	}

	@Override
	public Priority getPriority(final URI uri, ProgressMonitor progressMonitor) {
		return getAndParse(uri, priorityJsonParser, progressMonitor);
	}

	@Override
	public Iterable<Priority> getPriorities(ProgressMonitor progressMonitor) {
		final URI uri = UriBuilder.fromUri(baseUri).path("priority").build();
		return getAndParse(uri, prioritiesJsonParser, progressMonitor);
	}

	@Override
	public Resolution getResolution(URI uri, ProgressMonitor progressMonitor) {
		return getAndParse(uri, resolutionJsonParser, progressMonitor);
	}

	@Override
	public Iterable<Resolution> getResolutions(ProgressMonitor progressMonitor) {
		final URI uri = UriBuilder.fromUri(baseUri).path("resolution").build();
		return getAndParse(uri, resolutionsJsonParser, progressMonitor);
	}

	@Override
	public ServerInfo getServerInfo(ProgressMonitor progressMonitor) {
		final URI uri = UriBuilder.fromUri(baseUri).path(SERVER_INFO_RESOURCE).build();
		return getAndParse(uri, serverInfoJsonParser, progressMonitor);
	}

    @Override
    public SessionInfo getSessionInfo(ProgressMonitor progressMonitor) {
		URI sessionUri = UriBuilder.fromUri(baseUri.toString().replace("api", "auth")).path("/session").build();
		return getAndParse(sessionUri, sessionInfoJsonParser, progressMonitor);
    }
}

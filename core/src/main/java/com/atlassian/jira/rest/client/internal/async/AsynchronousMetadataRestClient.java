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
package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.IssueTypeScheme;
import com.atlassian.jira.rest.client.api.domain.IssuelinksType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.input.IssueTypeSchemeInput;
import com.atlassian.jira.rest.client.api.domain.input.ProjectIdOrKey;
import com.atlassian.jira.rest.client.internal.json.FieldJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.IssueLinkTypesJsonParser;
import com.atlassian.jira.rest.client.internal.json.IssueTypeJsonParser;
import com.atlassian.jira.rest.client.internal.json.IssueTypeSchemeJsonParser;
import com.atlassian.jira.rest.client.internal.json.IssueTypeSchemeListJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.PriorityJsonParser;
import com.atlassian.jira.rest.client.internal.json.ProjectJsonParser;
import com.atlassian.jira.rest.client.internal.json.ResolutionJsonParser;
import com.atlassian.jira.rest.client.internal.json.ServerInfoJsonParser;
import com.atlassian.jira.rest.client.internal.json.StatusJsonParser;
import com.atlassian.jira.rest.client.internal.json.gen.IssueTypeSchemeInputJsonGenerator;
import com.atlassian.util.concurrent.Promise;
import com.google.common.collect.Lists;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * Asynchronous implementation of MetadataRestClient.
 *
 * @since v2.0
 */
public class AsynchronousMetadataRestClient extends AbstractAsynchronousRestClient implements MetadataRestClient {

    private static final String ISSUE_TYPE_SCHEME = "issuetypescheme";
    private static final String SERVER_INFO_RESOURCE = "/serverInfo";
    private final ServerInfoJsonParser serverInfoJsonParser = new ServerInfoJsonParser();
    private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
    private final GenericJsonArrayParser<IssueType> issueTypesJsonParser = GenericJsonArrayParser.create(issueTypeJsonParser);
    private final StatusJsonParser statusJsonParser = new StatusJsonParser();
    private final GenericJsonArrayParser<Status> statusesJsonParser = GenericJsonArrayParser.create(statusJsonParser);
    private final PriorityJsonParser priorityJsonParser = new PriorityJsonParser();
    private final GenericJsonArrayParser<Priority> prioritiesJsonParser = GenericJsonArrayParser.create(priorityJsonParser);
    private final ResolutionJsonParser resolutionJsonParser = new ResolutionJsonParser();
    private final GenericJsonArrayParser<Resolution> resolutionsJsonParser = GenericJsonArrayParser.create(resolutionJsonParser);
    private final IssueLinkTypesJsonParser issueLinkTypesJsonParser = new IssueLinkTypesJsonParser();
    private final JsonArrayParser<Iterable<Field>> fieldsJsonParser = FieldJsonParser.createFieldsArrayParser();
    private final URI baseUri;

    public AsynchronousMetadataRestClient(final URI baseUri, HttpClient httpClient) {
        super(httpClient);
        this.baseUri = baseUri;

    }

    @Override
    public Promise<IssueType> getIssueType(final URI uri) {
        return getAndParse(uri, issueTypeJsonParser);
    }

    @Override
    public Promise<Iterable<IssueType>> getIssueTypes() {
        final URI uri = UriBuilder.fromUri(baseUri).path("issuetype").build();
        return getAndParse(uri, issueTypesJsonParser);
    }

    @Override
    public Promise<Iterable<IssuelinksType>> getIssueLinkTypes() {
        final URI uri = UriBuilder.fromUri(baseUri).path("issueLinkType").build();
        return getAndParse(uri, issueLinkTypesJsonParser);
    }

    @Override
    public Promise<Status> getStatus(URI uri) {
        return getAndParse(uri, statusJsonParser);
    }

    @Override
    public Promise<Iterable<Status>> getStatuses() {
        final URI uri = UriBuilder.fromUri(baseUri).path("status").build();
        return getAndParse(uri, statusesJsonParser);
    }

    @Override
    public Promise<Priority> getPriority(URI uri) {
        return getAndParse(uri, priorityJsonParser);
    }

    @Override
    public Promise<Iterable<Priority>> getPriorities() {
        final URI uri = UriBuilder.fromUri(baseUri).path("priority").build();
        return getAndParse(uri, prioritiesJsonParser);
    }

    @Override
    public Promise<Resolution> getResolution(URI uri) {
        return getAndParse(uri, resolutionJsonParser);
    }

    @Override
    public Promise<Iterable<Resolution>> getResolutions() {
        final URI uri = UriBuilder.fromUri(baseUri).path("resolution").build();
        return getAndParse(uri, resolutionsJsonParser);
    }

    @Override
    public Promise<ServerInfo> getServerInfo() {
        final URI serverInfoUri = UriBuilder.fromUri(baseUri).path(SERVER_INFO_RESOURCE).build();
        return getAndParse(serverInfoUri, serverInfoJsonParser);
    }

    @Override
    public Promise<Iterable<Field>> getFields() {
        final URI uri = UriBuilder.fromUri(baseUri).path("field").build();
        return getAndParse(uri, fieldsJsonParser);
    }


    @Override
    public Promise<IssueTypeScheme> createIssueTypeScheme(IssueTypeSchemeInput scheme) {
        final URI uri = UriBuilder.fromUri(baseUri).path(ISSUE_TYPE_SCHEME).build();
        return postAndParse(uri, scheme, new IssueTypeSchemeInputJsonGenerator(), new IssueTypeSchemeJsonParser());
    }

    @Override
    public Promise<Iterable<IssueTypeScheme>> getAllIssueTypeSchemes() {
        final URI uri = UriBuilder.fromUri(baseUri).path(ISSUE_TYPE_SCHEME)
                .queryParam("expand", "schemes.defaultIssueType", "schemes.issueTypes")
                .build();
        return getAndParse(uri, new IssueTypeSchemeListJsonParser())
                .map(itsList -> itsList.getIssueTypeSchemes());
    }

    @Override
    public Promise<IssueTypeScheme> getIssueTypeScheme(long id) {
        final URI uri = UriBuilder.fromUri(baseUri).path(ISSUE_TYPE_SCHEME).path(Long.toString(id))
                .queryParam("expand", "defaultIssueType", "issueTypes")
                .build();
        return getAndParse(uri, new IssueTypeSchemeJsonParser());
    }



    @Override
    public Promise<Iterable<Project>> getProjectsAssociatedWithIssueTypeScheme(long schemeId) {
        final URI uri = UriBuilder.fromUri(baseUri)
                .path(ISSUE_TYPE_SCHEME)
                .path(Long.toString(schemeId))
                .path("associations")
                .build();

        return getAndParse(uri, GenericJsonArrayParser.create(new ProjectJsonParser()));
    }

    @Override
    public Promise<IssueTypeScheme> updateIssueTypeScheme(long id, IssueTypeSchemeInput updatedScheme) {
        final URI uri = UriBuilder.fromUri(baseUri)
                .path(ISSUE_TYPE_SCHEME)
                .path(Long.toString(id))
                .build();

        return putAndParse(uri, updatedScheme, new IssueTypeSchemeInputJsonGenerator(), new IssueTypeSchemeJsonParser());
    }

    @Override
    public Promise<Void> deleteIssueTypeScheme(long id) {
        final URI uri = UriBuilder.fromUri(baseUri)
                .path(ISSUE_TYPE_SCHEME)
                .path(Long.toString(id))
                .build();

        return delete(uri);
    }

    @Override
    public Promise<Void> addProjectAssociatonsToScheme(long schemeId, String... projKeys) {

        final URI uri = UriBuilder.fromUri(baseUri)
                .path(ISSUE_TYPE_SCHEME)
                .path(Long.toString(schemeId))
                .path("associations")
                .build();

        List<String> keysOrIds = projKeys != null ? Lists.newArrayList(projKeys) : Collections.emptyList();

        return post(uri, keysOrIds,
                l -> {
                    JSONArray arry = new JSONArray();
                    l.forEach(e -> arry.put(e));
                    return new JSONObject().put("idsOrKeys", arry);
                });
    }

    @Override
    public Promise<Void> setProjectAssociationsForScheme(long schemeId, String... projKeys) {
        final URI uri = UriBuilder.fromUri(baseUri)
                .path(ISSUE_TYPE_SCHEME)
                .path(Long.toString(schemeId))
                .path("associations")
                .build();

        List<String> keysOrIds = projKeys != null ? Lists.newArrayList(projKeys) : Collections.emptyList();

        return put(uri, keysOrIds,
                l -> {
                    JSONArray arry = new JSONArray();
                    l.forEach(e -> arry.put(e));
                    JSONObject obj = new JSONObject().put("idsOrKeys", arry);
                    return obj;
                });
    }


    @Override
    public Promise<Void> unassignProjectFromScheme(long schemeId, String projectKey) {

        final URI uri = UriBuilder.fromUri(baseUri)
                .path(ISSUE_TYPE_SCHEME)
                .path(Long.toString(schemeId))
                .path("associations")
                .path(projectKey)
                .build();

        return delete(uri);
    }

    @Override
    public Promise<Void> unassignAllProjectsFromScheme(long schemeId) {

       final URI uri = UriBuilder.fromUri(baseUri)
                .path(ISSUE_TYPE_SCHEME)
                .path(Long.toString(schemeId))
                .path("associations")
                .build();

       return delete(uri);
    }
}

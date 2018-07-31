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

package com.atlassian.jira.rest.client.api;

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
import com.atlassian.util.concurrent.Promise;
import com.google.common.collect.Lists;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Serves information about JIRA metadata like server information, issue types defined, stati, priorities and resolutions.
 * This data constitutes a data dictionary which then JIRA issues base on.
 *
 * @since v2.0
 */
public interface MetadataRestClient {

    /**
     * Retrieves from the server complete information about selected issue type
     *
     * @param uri URI to issue type resource (one can get it e.g. from <code>self</code> attribute
     *            of issueType field of an issue).
     * @return complete information about issue type resource
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    Promise<IssueType> getIssueType(URI uri);

    /**
     * Retrieves from the server complete list of available issue type
     *
     * @return complete information about issue type resource
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     * @since com.atlassian.jira.rest.client.api 1.0, server 5.0
     */
    Promise<Iterable<IssueType>> getIssueTypes();

    /**
     * Retrieves from the server complete list of available issue types
     *
     * @return list of available issue types for this JIRA instance
     * @throws RestClientException in case of problems (if linking is disabled on the server, connectivity, malformed messages, etc.)
     * @since server 4.3, com.atlassian.jira.rest.client.api 0.5
     */
    Promise<Iterable<IssuelinksType>> getIssueLinkTypes();

    /**
     * Retrieves complete information about selected status
     *
     * @param uri URI to this status resource (one can get it e.g. from <code>self</code> attribute
     *            of <code>status</code> field of an issue)
     * @return complete information about the selected status
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    Promise<Status> getStatus(URI uri);

    /**
     * Retrieves lists of available statuses with complete information about them
     *
     * @return Lists of complete information about available statuses
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    Promise<Iterable<Status>> getStatuses();

    /**
     * Retrieves from the server complete information about selected priority
     *
     * @param uri URI for the priority resource
     * @return complete information about the selected priority
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    Promise<Priority> getPriority(URI uri);

    /**
     * Retrieves from the server complete list of available priorities
     *
     * @return complete information about the selected priority
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     * @since com.atlassian.jira.rest.client.api 1.0, server 5.0
     */
    Promise<Iterable<Priority>> getPriorities();

    /**
     * Retrieves from the server complete information about selected resolution
     *
     * @param uri URI for the resolution resource
     * @return complete information about the selected resolution
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    Promise<Resolution> getResolution(URI uri);

    /**
     * Retrieves from the server complete information about selected resolution
     *
     * @return complete information about the selected resolution
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     * @since com.atlassian.jira.rest.client.api 1.0, server 5.0
     */
    Promise<Iterable<Resolution>> getResolutions();

    /**
     * Retrieves information about this JIRA instance
     *
     * @return information about this JIRA instance
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    Promise<ServerInfo> getServerInfo();

    /**
     * Retrieves information about JIRA custom and system fields.
     *
     * @return information about JIRA custom and system fields.
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    Promise<Iterable<Field>> getFields();



    //sju:TODO here or elsewhere?

    //TODO:docs --> @Since ___?
    Promise<IssueTypeScheme> createIssueTypeScheme(IssueTypeSchemeInput scheme);

    Promise<Iterable<IssueTypeScheme>> getAllIssueTypeSchemes();

    Promise<IssueTypeScheme> getIssueTypeScheme(long id);

    Promise<Iterable<Project>> getProjectsAssociatedWithIssueTypeScheme(long schemeId);

    Promise<IssueTypeScheme> updateIssueTypeScheme(long id, IssueTypeSchemeInput updatedScheme);

    Promise<Void> deleteIssueTypeScheme(long id);

    //only one; delete doesn't make sense w/a body. Could do multi-delete via PUT, I suppose.
    Promise<Void> unassignProjectFromScheme(long schemeId, String projKey);

    default Promise<Void> unassignProjectFromScheme(long schemeId, Long projId) {
        return unassignProjectFromScheme(schemeId, Long.toString(projId));
    }


    Promise<Void> unassignAllProjectsFromScheme(long schemeId);


    Promise<Void> addProjectAssociatonsToScheme(long schemeId, String... projKeys);

    default Promise<Void> addProjectAssociatonsToScheme(long schemeId, Long... projIds) {
        return addProjectAssociatonsToScheme(schemeId, Arrays.asList(projIds)
                                                            .stream()
                                                            .map(id -> Long.toString(id))
                                                            .collect(Collectors.toList())
                                                            .toArray(new String[0]));
    }


    Promise<Void> setProjectAssociationsForScheme(long schemeId, String... projKeys);

    default Promise<Void> setProjectAssociationsForScheme(long schemeId, Long... projIds) {
        return  setProjectAssociationsForScheme(schemeId, Arrays.asList(projIds)
                .stream()
                .map(id -> Long.toString(id))
                .collect(Collectors.toList())
                .toArray(new String[0]));
    }
}

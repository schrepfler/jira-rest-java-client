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

package it;

import com.atlassian.jira.rest.client.IntegrationTestUtil;
import com.atlassian.jira.rest.client.TestUtil;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.IssueTypeScheme;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the various getters related to issue type schemes.
 * Read-only because these tests mustn't change anything on server side, as jira is restored only once.
 *
 * @since 5.2
 */
// Ignore "May produce NPE" warnings, as we know what we are doing in tests
@SuppressWarnings("ConstantConditions")
public class MetadataClientIssueTypeSchemeReadOnlyTest extends AbstractAsynchronousMetadataRestClientTest {

    private static boolean alreadyRestored;


    @Before
    public void setup() {

        setAdmin();//some unhappy paths stray from this

        if (!alreadyRestored) {
            //data from jira 8.0
            IntegrationTestUtil.restoreAppropriateJiraData("jira-dump-issue-type-scheme-tests.xml");
            alreadyRestored = true;
        }
    }

    @Test
    public void testGetAllIssueTypeSchemes() {

        final Iterable<IssueTypeScheme> schemes = client.getMetadataClient().getAllIssueTypeSchemes().claim();

        assertEquals(7, Iterables.size(schemes));

        Set<String> expected = Sets.newHashSet("Default Issue Type Scheme",
                "Tasks Only Scheme",
                "Only Tasks And Bugs Scheme",
                "Tasks Bugs And Subtasks Scheme",
                "All Scrum Types Scheme",
                "All Types But No Default Scheme",
                "No Bugs Scheme");

        assertEquals(expected, Sets.newHashSet(schemes).stream().map(its -> its.getName()).collect(Collectors.toSet()));
    }

    @Test
    public void testGetAllIssueTypeSchemes_UnhappyPaths() {
        unhappyAuthenticationAndAuthorization(() -> client.getMetadataClient().getAllIssueTypeSchemes().claim());
    }

    @Test
    public void testGetIssueTypeScheme() {
        final IssueTypeScheme scheme = client.getMetadataClient().getIssueTypeScheme(ALL_SCRUM_TYPES_SCHEME).claim();

        assertEquals("All Scrum Types Scheme", scheme.getName());

        IssueType defType = scheme.getDefaultIssueType();
        assertEquals("Story", defType.getName());
        assertEquals(Long.valueOf(10001), defType.getId());

        assertEquals("the lot of them", scheme.getDescription());

        List<IssueType> types = scheme.getIssueTypes();


        List<String> expected = Lists.newArrayList("Task", "Sub-task", "Story", "Bug", "Epic");

        assertEquals(expected, types.stream().map(t -> t.getName()).collect(Collectors.toList()));

        assertEquals(defType, types.get(2));
    }

    @Test
    public void testGetIssueTypeSchemeWithoutDefault() {

        final IssueTypeScheme scheme = client.getMetadataClient().getIssueTypeScheme(ALL_TYPES_BUT_NO_DEFAULT_SCHEME).claim();
        assertEquals("All Types But No Default Scheme", scheme.getName());

        assertNull(scheme.getDefaultIssueType());

        assertEquals("Default Issue Type Isn't Specified/ None", scheme.getDescription());

        List<String> expected =
                Lists.newArrayList("Task", "Sub-task", "Story", "Bug", "Epic", "Improvement", "New Feature");
        assertEquals(expected, scheme.getIssueTypes().stream().map(t -> t.getName()).collect(Collectors.toList()));
    }

    @Test
    public void testGetIssueTypeScheme_UnhappyPaths() {
        unhappyAuthenticationAndAuthorization(() -> client.getMetadataClient().getIssueTypeScheme(ALL_SCRUM_TYPES_SCHEME).claim());

        TestUtil.assertErrorCode(Response.Status.NOT_FOUND, () -> client.getMetadataClient().getIssueTypeScheme(99L).claim());
    }


    @Test
    public void testGetAssociatedProjects() {
        final Iterable<Project> twoProjects = //Tasks Only Scheme
                client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim();
        final Set<String> expected = Sets.newHashSet("TasksProject", "TasksProject2");
        assertEquals(expected,
                Sets.newHashSet(twoProjects.iterator()).stream().map(p -> p.getName()).collect(Collectors.toSet()));


        final Iterable<Project> oneProj = //Only Tasks And Bugs Scheme
                client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();
        assertEquals("TasksBugs", Iterables.getOnlyElement(oneProj).getName());


        final Iterable<Project> noProjects = //Tasks Bugs And Subtasks Scheme
                client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_BUGS_AND_SUBTASKS_SCHEME).claim();
        assertTrue(Iterables.isEmpty(noProjects));

    }

    @Test
    public void testGetAssociatedProjects_UnhappyPaths() {

        unhappyAuthenticationAndAuthorization(() -> client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim());

        TestUtil.assertErrorCode(Response.Status.NOT_FOUND,
                () -> client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(999).claim());
    }

}

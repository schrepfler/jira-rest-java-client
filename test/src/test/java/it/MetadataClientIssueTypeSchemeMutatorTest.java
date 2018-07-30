/*
 * Copyright (C) 2018 Atlassian
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
import com.atlassian.jira.rest.client.api.domain.input.IssueTypeSchemeInput;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests that do change state.
 *
 */
// Ignore "May produce NPE" warnings, as we know what we are doing in tests
@SuppressWarnings("ConstantConditions")
public class MetadataClientIssueTypeSchemeMutatorTest extends AbstractAsynchronousRestClientTest {

    //Scheme Ids
    private final static long DEFAULT_SCHEME_ID = 10000L;
    private final static long NO_BUGS_SCHEME = 10300L;
    private final static long TASKS_AND_BUGS_SCHEME = 10202L;
    private final static long TASKS_ONLY_SCHEME = 10201L;

    //Issue Types
    private final static long BUG = 10102L;
    private final static long EPIC = 10000L;
    private final static long IMPROVEMENT = 10103;
    private final static long STORY = 10001L;
    private final static long TASK = 10100L;

    @Before
    public void setup() {
        //data from jira 8.0
        IntegrationTestUtil.restoreAppropriateJiraData("jira-dump-issue-type-scheme-tests.xml");
    }

    @Test
    public void testCreateIssueTypeScheme() {

        System.out.println("HIT");

        //start out w/just 7--default + 6 custom
        assertEquals(7,
                Iterables.size(client.getMetadataClient().getAllIssueTypeSchemes().claim()));

        //TODO: how do we want to handle bad input cases here?
        IssueTypeScheme schemer = client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("foo name", "some description",
                        Arrays.asList(TASK, STORY, BUG), 10102L)).claim();

        assertEquals("foo name", schemer.getName());
        assertEquals("some description", schemer.getDescription());
        assertEquals(Long.valueOf(BUG), schemer.getDefaultIssueType().getId());
        assertEquals("Bug", schemer.getDefaultIssueType().getName());
        assertEquals(Arrays.asList(TASK, STORY, BUG),
                schemer.getIssueTypes().stream().map(it -> it.getId()).collect(Collectors.toList()));

        //Now our total count should've been incremented
        assertEquals(8,
                Iterables.size(client.getMetadataClient().getAllIssueTypeSchemes().claim()));

        //and ensure that what was returned from the Post matches what gets stored in JIRA
        assertEquals(schemer, client.getMetadataClient().getIssueTypeScheme(schemer.getId()).claim());
    }


    @Test
    public void testCreateIssueTypeSchemeWithBadInput() {
        //TODO: little helper that drops things in a runnable and then returns a result.
        //a way to test lots of different error permutations in one method.

        //issue types that don't exist
        //a default type that isn't in the list of types
        //null name
        //null description?

        fail("test me");
    }


    @Test
    public void testCreateIssueTypeSchemeWithNoDefaultType() {
        IssueTypeScheme schemer = client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("bar name", "bazz description", Arrays.asList(STORY, TASK))).claim();

        assertEquals("bar name", schemer.getName());
        assertEquals("bazz description", schemer.getDescription());
        assertNull(schemer.getDefaultIssueType());//sju: TODO null defaults are ok; what of empty ones?


        assertEquals("bar name", schemer.getName());
        assertEquals("bazz description", schemer.getDescription());
        assertNull(schemer.getDefaultIssueType());
        assertEquals(Arrays.asList(STORY, TASK),
                schemer.getIssueTypes().stream().map(it -> it.getId()).collect(Collectors.toList()));

        //Now our total count should've been incremented
        assertEquals(8,
                Iterables.size(client.getMetadataClient().getAllIssueTypeSchemes().claim()));

        //and ensure that what was returned from the Post matches what gets stored in JIRA
        assertEquals(schemer, client.getMetadataClient().getIssueTypeScheme(schemer.getId()).claim());
    }


    @Test
    public void testDeleteIssueTypeScheme() {
        //start off with two IssueTypeSchemes
        assertEquals(7,
                Iterables.size(client.getMetadataClient().getAllIssueTypeSchemes().claim()));

        //even though it has two projects assigned to it, deleting "Tasks Only Scheme" shouldn't be a problem
        //the project will just get re-assigned to the default IssueTypeScheme which contains all IssueTypes

        client.getMetadataClient().deleteIssueTypeScheme(TASKS_ONLY_SCHEME).claim();


        Iterable<IssueTypeScheme> remaining = client.getMetadataClient().getAllIssueTypeSchemes().claim();
        assertEquals(6, Iterables.size(remaining));

        assertEquals(0,
                Lists.newArrayList(remaining).stream().filter(its -> its.getName().equals(TASKS_ONLY_SCHEME)).count());
    }

    @Test
    public void testDeleteDefaultIssueTypeScheme() {
        TestUtil.assertErrorCode(Response.Status.FORBIDDEN,
                () -> client.getMetadataClient().deleteIssueTypeScheme(DEFAULT_SCHEME_ID).claim());
    }

    @Test
    public void testUpdateIssueTypeScheme() {//happy path--no projects are assigned to this scheme

        IssueTypeSchemeInput input = new IssueTypeSchemeInput("New Name", "new description", Arrays.asList(STORY, EPIC));
        IssueTypeScheme updated = client.getMetadataClient().updateIssueTypeScheme(NO_BUGS_SCHEME, input).claim();

        assertEquals(Long.valueOf(NO_BUGS_SCHEME), updated.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("new description", updated.getDescription());
        assertNull(updated.getDefaultIssueType());
        assertEquals(Arrays.asList(STORY,EPIC),
                updated.getIssueTypes().stream().map(it -> it.getId()).collect(Collectors.toList()));

        assertEquals(updated, client.getMetadataClient().getIssueTypeScheme(NO_BUGS_SCHEME).claim());
    }

    @Test
    public void testUpdateIssueTypeSchemeUnhappyPaths() {

        //non-existent issue type scheme id
        fail("test me");
        //Missing required pieces of the body


        //default issue type isn't part of the list of issue types

        //migration would be required
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().assignSchemeToProject(TASKS_AND_BUGS_SCHEME, null).claim());
    }

    @Test
    public void testAssignSchemeToProject() {//happy path, smaller scheme to larger superset scheme
        final Iterable<Project> projects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();

        //check that we're starting off w/1 project associated
        Project onlyProj =  Iterables.getOnlyElement(projects);
        assertEquals("TasksBugs", onlyProj.getName());
        assertEquals("TSKBUGS", onlyProj.getKey());

        System.out.println("*****GOT HERE*******");

        //associate another project with the "Only Tasks And Bugs Scheme" IssueTypeScheme
        client.getMetadataClient().assignSchemeToProject(TASKS_AND_BUGS_SCHEME, "TSKS").claim();

        System.out.println("*****AND HERE*******");
        //make sure that the newly associated project does in fact show up on subsequent requests
        final Iterable<Project> updatedProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();
        assertEquals("Couldn't find the newly associated project", 2, Iterables.size(updatedProjects));
        assertEquals("Couldn't find the newly associated project", 1, Lists.newArrayList(updatedProjects).stream()
                .filter(p -> "TasksProject".equals(p.getName()))
                .count());


        //Now, do the test again, but using the proj ID rather than the key
        //associate a third project with the "Only Tasks And Bugs Scheme" IssueTypeScheme
        client.getMetadataClient().assignSchemeToProject(TASKS_AND_BUGS_SCHEME, 10100).claim(); //"TasksProject2"

        final Iterable<Project> newlyUpdatedProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();
        assertEquals("Couldn't find the newly associated project", 3, Iterables.size(newlyUpdatedProjects));
        assertEquals("Couldn't find the newly associated project", 1, Lists.newArrayList(newlyUpdatedProjects).stream()
                .filter(p -> "TasksProject2".equals(p.getName()))
                .count());
    }

    //sju::TODO: just put this into the general error-case bracket?
    @Test
    public void testAssignSchemeToProjectWithMigrationRequired() {

        assertEquals(1,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim()));


        //The 'All Scrum Types' project has existing issues that can't be forced into the smaller "Only Tasks And Bugs" IssueTypeScheme
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().assignSchemeToProject(TASKS_AND_BUGS_SCHEME, "ALLSCRUMTP").claim());
    }


}

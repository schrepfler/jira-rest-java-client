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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Tests that do change state.
 *
 */
// Ignore "May produce NPE" warnings, as we know what we are doing in tests
@SuppressWarnings("ConstantConditions")
public class MetadataClientIssueTypeSchemeMutatorTest extends AbstractAsynchronousRestClientTest {

    //Scheme Ids
    private final static long ALL_SCRUM_TYPES_SCHEME = 10001L;
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

    //projects
    private final static long TSKS_PROJECT_ID =  10000L;
    private final static long TSKSBUGS_PROJECT_ID =  10001L;

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
                Lists.newArrayList(remaining).stream().filter(its -> its.getName().equals("Tasks Only Scheme")).count());
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
    }

    @Test
    public void testAddProjectAssociationsToScheme() {

        final Iterable<Project> initialProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(ALL_SCRUM_TYPES_SCHEME).claim();

        //check that we're starting off w/1 project associated
        Project onlyProj =  Iterables.getOnlyElement(initialProjects);
        assertEquals("All Scrum Types Project", onlyProj.getName());
        assertEquals("ALLSCRUMTP", onlyProj.getKey());

        System.out.println("*****GOT HERE*******");

        //associate two more projects with the "All Scrum Types" IssueTypeScheme
        client.getMetadataClient().addProjectAssociatonsToScheme(ALL_SCRUM_TYPES_SCHEME, "TSKS", "TASKS2").claim();

        System.out.println("*****AND HERE*******");
        //make sure that the newly associated projects do in fact show up on subsequent requests
        final Iterable<Project> updatedProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(ALL_SCRUM_TYPES_SCHEME).claim();
        assertEquals("Couldn't find the newly associated projects", 3, Iterables.size(updatedProjects));
        assertEquals("Couldn't find the newly associated projects", 2, Lists.newArrayList(updatedProjects).stream()
                .filter(p -> "TasksProject".equals(p.getName()) || "TasksProject2".equals(p.getName()))
                .count());


        //Now, do the test again, but using the proj ID rather than the key
        //associate a third project with the "All Scrum Types" IssueTypeScheme
        client.getMetadataClient().addProjectAssociatonsToScheme(ALL_SCRUM_TYPES_SCHEME, TSKSBUGS_PROJECT_ID).claim();

        final Iterable<Project> newlyUpdatedProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(ALL_SCRUM_TYPES_SCHEME).claim();
        assertEquals("Couldn't find the newly associated project", 4, Iterables.size(newlyUpdatedProjects));
        assertEquals("Couldn't find the newly associated project", 1, Lists.newArrayList(newlyUpdatedProjects).stream()
                .filter(p -> "TasksBugs".equals(p.getName()))
                .count());
    }

    @Test
    public void testAddProjectAssociationsToSchemeUnhappyPaths() {
        fail("test me");


        assertEquals(1,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim()));


        //The 'All Scrum Types' project has existing issues that can't be forced into the smaller "Only Tasks And Bugs" IssueTypeScheme
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().addProjectAssociatonsToScheme(TASKS_AND_BUGS_SCHEME, "ALLSCRUMTP", "TSKS", "TASKS2").claim());
    }

    @Test
    public void testSetProjectAssociationsForScheme() {
        final Iterable<Project> initialProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();

        //check that we're starting off w/1 project associated
        Project onlyProj =  Iterables.getOnlyElement(initialProjects);
        assertEquals("TasksBugs", onlyProj.getName());

        System.out.println("*****GOT HERE*******");

        //associate 0  projects with the "Only Tasks And Bugs" IssueTypeScheme--effectively a deleteAll
        client.getMetadataClient().setProjectAssociationsForScheme(TASKS_AND_BUGS_SCHEME, new String[]{}).claim();
        final Iterable<Project> emptyProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();
        assertEquals("shouldn't have any projects associated at this point", 0, Iterables.size(emptyProjects));


        //Assign 2 at once
        client.getMetadataClient().setProjectAssociationsForScheme(TASKS_AND_BUGS_SCHEME, "TSKS", "TASKS2").claim();

        final Iterable<Project> newlyUpdatedProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();
        assertEquals("Couldn't find the newly associated projects", 2, Iterables.size(newlyUpdatedProjects));
        assertEquals("Couldn't find the newly associated projects", 2,
                Lists.newArrayList(newlyUpdatedProjects).stream()
                    .filter(p -> "Tasks".equals(p.getName()) || "Tasks2".equals(p.getName()))
                    .count());


        //Finally, set the original project as the only association, using its ID
        client.getMetadataClient().setProjectAssociationsForScheme(TASKS_AND_BUGS_SCHEME, TSKSBUGS_PROJECT_ID).claim();

        final Iterable<Project> oneProjAgain = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();
        assertEquals("expected just the one project, 'TASKSBUGS'", 1, Iterables.size(oneProjAgain));
        assertEquals("expected just the one project, 'TASKSBUGS'", 1,
                Lists.newArrayList(oneProjAgain).stream()
                                                .filter(p -> "TasksBugs".equals(p.getName()))
                                                .count());
    }

    @Test
    public void testSetProjectAssociationsToSchemeUnhappyPaths() {

        //everywhere: what of empty strings where they're allowed??
        fail("test me");
    }



    @Test
    public void testUnAssignProjectFromScheme() {

        //first, remove Tasks2 project using KEY
        assertEquals(2,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim()));
        client.getMetadataClient().unassignProjectFromScheme(TASKS_ONLY_SCHEME, "TASKS2");
        assertEquals("TASKS",
                Iterables.getOnlyElement(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim()).getKey());


        //Then, remove the Tasks project by its ID
        client.getMetadataClient().unassignProjectFromScheme(TASKS_ONLY_SCHEME, TSKS_PROJECT_ID);
        assertEquals(0,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim()));
    }

    @Test
    public void testUnAssignProjectFromSchemeUnHappyPaths() {
        //can't unassign from the default issue type scheme!

        //unknown scheme

        //unknown project

        //repeats?
        fail("test me");
    }


    @Test
    public void testUnAssignAllProjectsFromScheme() {
        assertEquals(2,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim()));
        client.getMetadataClient().unassignAllProjectsFromScheme(TASKS_ONLY_SCHEME).claim();
        assertEquals(0,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim()));
    }

    @Test
    public void testUnAssignAllProjectsFromSchemeUnHappyPaths() {
        //can't unassign from the default issue type scheme!

        //unknown scheme

        //unknown project

        //repeats?
        fail("test me");
    }
}

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
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests that <i><b>do</b></i> change state.
 *
 */
// Ignore "May produce NPE" warnings, as we know what we are doing in tests
@SuppressWarnings("ConstantConditions")
public class MetadataClientIssueTypeSchemeMutatorTest extends AbstractAsynchronousMetadataRestClientTest {


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
    public void testCreateIssueTypeScheme_UnhappyPaths() {

        unhappyAuthenticationAndAuthorization( () -> client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("foo name", "some description",
                        Arrays.asList(TASK, STORY, BUG), 10102L)).claim());


        //issue types that don't exist
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST, () -> client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("foo name", "some description",
                        Arrays.asList(2348L), 2348L)).claim());

        //empty list of issue types
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST, () -> client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("foo name", "some description",
                        Collections.emptyList() )).claim());

        //a default type that isn't in the list of types
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST, () -> client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("foo name", "some description",
                        Arrays.asList(STORY, TASK), EPIC)).claim());

        //null name
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST, () -> client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput(null, "some description",
                        Arrays.asList(STORY, TASK), STORY)).claim());

        //empty string name
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST, () -> client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("", "some description",
                        Arrays.asList(STORY, TASK), STORY)).claim());
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
    public void testDeleteIssueTypeScheme_UnhappyPaths() {

        unhappyAuthenticationAndAuthorization( () -> client.getMetadataClient().deleteIssueTypeScheme(TASKS_ONLY_SCHEME).claim());

        //try to delete the default scheme--not a good idea
        TestUtil.assertErrorCode(Response.Status.FORBIDDEN,
                () -> client.getMetadataClient().deleteIssueTypeScheme(DEFAULT_SCHEME_ID).claim());

        //can't find the scheme to delete
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND,
                () -> client.getMetadataClient().deleteIssueTypeScheme(999).claim());
    }

    @Test
    public void testUpdateIssueTypeSchemeWithNoProjects() {//happy path--no projects are assigned to this scheme

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
    public void testUpdateIssueTypeSchemeNoMigration() {//happy path--associated projects are compatible with the issue type changes

        assertEquals(1,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(ALL_TYPES_BUT_NO_DEFAULT_SCHEME).claim()));

        IssueTypeSchemeInput noBugsInput = new IssueTypeSchemeInput("New Name", "new description",
                Arrays.asList(TASK, STORY, EPIC, IMPROVEMENT, FEATURE));

        //the NoBugsCreated project is currently assigned to this scheme--removing bugs as an option should not cause a migration
        IssueTypeScheme updated = client.getMetadataClient().updateIssueTypeScheme(ALL_TYPES_BUT_NO_DEFAULT_SCHEME, noBugsInput).claim();

        assertEquals(Long.valueOf(ALL_TYPES_BUT_NO_DEFAULT_SCHEME), updated.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("new description", updated.getDescription());
        assertNull(updated.getDefaultIssueType());
        assertEquals(Arrays.asList(TASK, STORY, EPIC, IMPROVEMENT, FEATURE),
                updated.getIssueTypes().stream().map(it -> it.getId()).collect(Collectors.toList()));

        assertEquals(updated, client.getMetadataClient().getIssueTypeScheme(ALL_TYPES_BUT_NO_DEFAULT_SCHEME).claim());
    }

    @Test
    public void testUpdateIssueTypeScheme_UnhappyPaths() {

        IssueTypeSchemeInput input = new IssueTypeSchemeInput("New Name", "new description", Arrays.asList(STORY, EPIC));
        unhappyAuthenticationAndAuthorization(() -> client.getMetadataClient().updateIssueTypeScheme(NO_BUGS_SCHEME, input).claim());

        //non-existent issue type scheme id
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND, () -> client.getMetadataClient().updateIssueTypeScheme(777, input).claim());


        //no issue types specified
        IssueTypeSchemeInput noTypesInput = new IssueTypeSchemeInput("New Name", "new description", Collections.emptyList());
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST, () -> client.getMetadataClient().updateIssueTypeScheme(TASKS_ONLY_SCHEME, input).claim());

        //issue types that don't exist
        IssueTypeSchemeInput nonexistentTypesInput = new IssueTypeSchemeInput("New Name", "new description", Arrays.asList(TASK, 999L), TASK);
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND, () -> client.getMetadataClient().updateIssueTypeScheme(TASKS_ONLY_SCHEME, nonexistentTypesInput).claim());

        //default issue type isn't part of the list of issue types
        IssueTypeSchemeInput invalidDefaultInput = new IssueTypeSchemeInput("New Name", "new description", Arrays.asList(TASK,BUG), EPIC);
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND, () -> client.getMetadataClient().updateIssueTypeScheme(TASKS_ONLY_SCHEME, invalidDefaultInput).claim());

        //null name
        IssueTypeSchemeInput nullNameInput = new IssueTypeSchemeInput(null, "new description", Arrays.asList(TASK,BUG, STORY), STORY);
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND, () -> client.getMetadataClient().updateIssueTypeScheme(TASKS_ONLY_SCHEME, nullNameInput).claim());

        //empty string name
        IssueTypeSchemeInput emptyNameInput = new IssueTypeSchemeInput("", "new description", Arrays.asList(TASK,BUG, STORY), STORY);
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND, () -> client.getMetadataClient().updateIssueTypeScheme(TASKS_ONLY_SCHEME, emptyNameInput).claim());


        //updates that would require a migration: here, the TasksBugs Project depends on both bug & task types--changing the scheme to only have tasks means migrating
        IssueTypeSchemeInput onlyTasksInput = new IssueTypeSchemeInput("name", "description", Arrays.asList(TASK), TASK);
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND, () -> client.getMetadataClient().updateIssueTypeScheme(TASKS_ONLY_SCHEME, onlyTasksInput).claim());

    }

    @Test
    public void testAddProjectAssociationsToScheme() {

        final Iterable<Project> initialProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(ALL_SCRUM_TYPES_SCHEME).claim();

        //check that we're starting off w/1 project associated
        Project onlyProj =  Iterables.getOnlyElement(initialProjects);
        assertEquals("AllScrumTypesProject", onlyProj.getName());
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
    public void testAddProjectAssociationsToScheme_UnhappyPaths() {

        unhappyAuthenticationAndAuthorization( () -> client.getMetadataClient().addProjectAssociatonsToScheme(NO_BUGS_SCHEME, "TSKS", "TASKS2").claim());


        //unknown issueTypeScheme
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND,
                () -> client.getMetadataClient().addProjectAssociatonsToScheme(789, "TSKS", "TASKS2").claim());

        //invalid project key(s)
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().addProjectAssociatonsToScheme(NO_BUGS_SCHEME, "TSKS", "GOJIRA").claim());

        //invalid project id(s))
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().addProjectAssociatonsToScheme(NO_BUGS_SCHEME, TSKS_PROJECT_ID, 989L).claim());

        //Any requests that would require a migration are returned as errors
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
        assertEquals("Couldn't find the newly associated projects by name", 2,
                Lists.newArrayList(newlyUpdatedProjects).stream()
                    .filter(p -> "TasksProject".equals(p.getName()) || "TasksProject2".equals(p.getName()))
                    .count());


        //Finally, set the original project as the only association, using its ID
        client.getMetadataClient().setProjectAssociationsForScheme(TASKS_AND_BUGS_SCHEME, TSKSBUGS_PROJECT_ID).claim();

        final Iterable<Project> oneProjAgain = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim();
        assertEquals("expected just one project", 1, Iterables.size(oneProjAgain));
        assertEquals("expected just the one project, 'TASKSBUGS'", 1,
                Lists.newArrayList(oneProjAgain).stream()
                                                .filter(p -> "TasksBugs".equals(p.getName()))
                                                .count());
    }

    @Test
    public void testSetProjectAssociationsToScheme_UnhappyPaths() {

        unhappyAuthenticationAndAuthorization(() -> client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_AND_BUGS_SCHEME).claim());

        //unknown issueTypeScheme
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND,
                () -> client.getMetadataClient().setProjectAssociationsForScheme(789, "TSKS", "TASKS2").claim());

        //invalid project key(s)
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().setProjectAssociationsForScheme(NO_BUGS_SCHEME, "TSKS", "GOJIRA").claim());

        //invalid project id(s))
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().setProjectAssociationsForScheme(NO_BUGS_SCHEME, TSKS_PROJECT_ID, 989L).claim());

        //Any requests that would require a migration are returned as errors
        //The 'All Scrum Types' project has existing issues that can't be forced into the smaller "Only Tasks And Bugs" IssueTypeScheme
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().setProjectAssociationsForScheme(TASKS_AND_BUGS_SCHEME, "ALLSCRUMTP", "TSKS", "TASKS2").claim());
    }



    @Test
    public void testUnAssignProjectFromScheme() {

        //first, remove Tasks2 project using KEY
        assertEquals(2,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim()));
        client.getMetadataClient().unassignProjectFromScheme(TASKS_ONLY_SCHEME, "TASKS2");

        Iterable<Project> projects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim();

        assertEquals(1, Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim()));
        System.out.println("$ize; " + Lists.newArrayList(projects).size());


        assertEquals("TSKS", Iterables.getOnlyElement(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim()).getKey());

        //Then, remove the Tasks project by its ID
        client.getMetadataClient().unassignProjectFromScheme(TASKS_ONLY_SCHEME, TSKS_PROJECT_ID);
//        Iterable<Project> decoy = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim();

        //@Konrad: --> respond with a 202 response in this sort of case, instead

        delay(1);
        assertEquals(0,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(TASKS_ONLY_SCHEME).claim()));
    }

    @Test
    public void testUnAssignProjectFromSchemeUnHappyPaths() {

        unhappyAuthenticationAndAuthorization( () ->
                client.getMetadataClient().unassignProjectFromScheme(TASKS_ONLY_SCHEME, "TASKS2").claim());

        //unknown scheme
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND,
                () -> client.getMetadataClient().unassignProjectFromScheme(798, "TASKS2"));

        //project not currently associated with the scheme
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND,
                () -> client.getMetadataClient().unassignProjectFromScheme(TASKS_ONLY_SCHEME, TSKSBUGS_PROJECT_ID));

        //unknown project key
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().unassignProjectFromScheme(TASKS_ONLY_SCHEME, "GOJIRA"));

        //unknown project id
        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().unassignProjectFromScheme(TASKS_ONLY_SCHEME, 789L));

        //can't unassign from the default issue type scheme (i think...TODO)??
        TestUtil.assertErrorCode(Response.Status.FORBIDDEN,
                () -> client.getMetadataClient().unassignProjectFromScheme(DEFAULT_SCHEME_ID, "TASKS2"));
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

        unhappyAuthenticationAndAuthorization( () ->
                client.getMetadataClient().unassignAllProjectsFromScheme(TASKS_ONLY_SCHEME).claim());

        //unknown scheme
        TestUtil.assertErrorCode(Response.Status.NOT_FOUND,
                () -> client.getMetadataClient().unassignAllProjectsFromScheme(798));

        //can't unassign from the default issue type scheme (i think...TODO)??
        TestUtil.assertErrorCode(Response.Status.FORBIDDEN,
                () -> client.getMetadataClient().unassignAllProjectsFromScheme(DEFAULT_SCHEME_ID));
    }
}

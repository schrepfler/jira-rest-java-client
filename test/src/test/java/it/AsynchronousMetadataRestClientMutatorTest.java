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

import com.atlassian.jira.nimblefunctests.annotation.JiraBuildNumberDependent;
import com.atlassian.jira.rest.client.IntegrationTestUtil;
import com.atlassian.jira.rest.client.TestUtil;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.EntityHelper;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.FieldSchema;
import com.atlassian.jira.rest.client.api.domain.FieldType;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.IssueTypeScheme;
import com.atlassian.jira.rest.client.api.domain.IssuelinksType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.IssueTypeSchemeInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.json.TestConstants;
import com.atlassian.jira.rest.client.test.matchers.RegularExpressionMatcher;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.atlassian.jira.rest.client.internal.ServerVersionConstants.BN_JIRA_4_3;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests that do change state.
 */
// Ignore "May produce NPE" warnings, as we know what we are doing in tests
@SuppressWarnings("ConstantConditions")
public class AsynchronousMetadataRestClientMutatorTest extends AbstractAsynchronousRestClientTest {

    //Scheme Ids
    private final static long DEFAULT_SCHEME_ID = 10000L;
    private final static long LITTLE_SCHEMER_ID = 10138L;



    @Before
    public void setup() {
        IntegrationTestUtil.restoreAppropriateJiraData("foo.xml", administration);
    }

    @Test
    public void testCreateIssueTypeScheme() {

        System.out.println("HIT");

        //start out w/just 2--default + 1 custom
        assertEquals(2,
                Iterables.size(client.getMetadataClient().getAllIssueTypeSchemes().claim()));

        //TODO: how do we want to handle bad input cases here?
        IssueTypeScheme schemer = client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("foo name", "some description", Arrays.asList(3L, 2L, 1L), 1L)).claim();

        assertEquals("foo name", schemer.getName());
        assertEquals("some description", schemer.getDescription());
        assertEquals(Long.valueOf(1L), schemer.getDefaultIssueType().getId());
        assertEquals("Bug", schemer.getDefaultIssueType().getName());
        assertEquals(Arrays.asList(3L,2L,1L),
                schemer.getIssueTypes().stream().map(it -> it.getId()).collect(Collectors.toList()));

        //Now our total count should've been incremented
        assertEquals(3,
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
    }


    @Test
    public void testCreateIssueTypeSchemeWithNoDefaultType() {
        IssueTypeScheme schemer = client.getMetadataClient().createIssueTypeScheme(
                new IssueTypeSchemeInput("bar name", "bazz description", Arrays.asList(1L))).claim();

        assertEquals("bar name", schemer.getName());
        assertEquals("bazz description", schemer.getDescription());
        assertNull(schemer.getDefaultIssueType());//sju: TODO null defaults are ok; what of empty ones?


        //Our total count should've still been incremented
        assertEquals(3,
                Iterables.size(client.getMetadataClient().getAllIssueTypeSchemes().claim()));
    }


    @Test
    public void testDeleteIssueTypeScheme() {
        //start off with two IssueTypeSchemes
        assertEquals(2,
                Iterables.size(client.getMetadataClient().getAllIssueTypeSchemes().claim()));

        //even though it has a project assigned to it, deleting "Little Schemer" shouldn't be a problem
        //the project will just get re-assigned to the default IssueTypeScheme which contains all IssueTypes

        client.getMetadataClient().deleteIssueTypeScheme(LITTLE_SCHEMER_ID).claim();

        assertEquals(1,
                Iterables.size(client.getMetadataClient().getAllIssueTypeSchemes().claim()));

        IssueTypeScheme onlyOne = Iterables.getOnlyElement(client.getMetadataClient().getAllIssueTypeSchemes().claim());
        assertEquals(Long.valueOf(DEFAULT_SCHEME_ID), onlyOne.getId());
    }

    @Test
    public void testDeleteDefaultIssueTypeScheme() {
        TestUtil.assertErrorCode(Response.Status.FORBIDDEN,
                () -> client.getMetadataClient().deleteIssueTypeScheme(DEFAULT_SCHEME_ID).claim());
    }

    @Test
    public void testUpdateIssueType() {//happy path

        IssueTypeSchemeInput input = new IssueTypeSchemeInput("New Name", "new description", Arrays.asList(1L, 2L, 3L));
        IssueTypeScheme updated = client.getMetadataClient().updateIssueTypeScheme(LITTLE_SCHEMER_ID, input).claim();

        assertEquals(Long.valueOf(LITTLE_SCHEMER_ID), updated.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("new description", updated.getDescription());
        assertNull(updated.getDefaultIssueType());
        assertEquals(Arrays.asList(1L,2L,3L),
                updated.getIssueTypes().stream().map(it -> it.getId()).collect(Collectors.toList()));

        assertEquals(updated, client.getMetadataClient().getIssueTypeScheme(LITTLE_SCHEMER_ID));
    }

    @Test
    public void testUpdateIssueTypeWithMigrationRequired() {

        fail("Add a new project + IST and then remove a type required by that project!");
    }

    @Test
    public void testAssignSchemeToProject() {
        final Iterable<Project> projects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(LITTLE_SCHEMER_ID).claim();

        //check that we're starting off w/1 project associated
        assertEquals(1, Iterables.size(projects));
        Project onlyProj =  Iterables.getOnlyElement(projects);
        assertEquals(Long.valueOf(10040), onlyProj.getId());
        assertEquals("IssueTypeScheme Of Its Own", onlyProj.getName());

        //associate another project with "Little Schemer" IssueTypeScheme
        client.getMetadataClient().assignSchemeToProject(LITTLE_SCHEMER_ID, 10030).claim();


        //make sure that the newly associated project does in fact show up on subsequent requests
        final Iterable<Project> updatedProjects = client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(LITTLE_SCHEMER_ID).claim();
        assertEquals("Couldn't find the newly associated project", 2, Iterables.size(updatedProjects));
        assertEquals("Couldn't find the newly associated project", 1, Lists.newArrayList(updatedProjects).stream()
                .filter(p -> p.getId() == 10030L && "Test of anonymous Attachments".equals(p.getName()))
                .count());

    }

    //sju::TODO: just put this into the general error-case bracket?
    @Test
    public void testAssignSchemeToProjectWithMigrationRequired() {

        assertEquals(1,
                Iterables.size(client.getMetadataClient().getProjectsAssociatedWithIssueTypeScheme(LITTLE_SCHEMER_ID).claim()));

        TestUtil.assertErrorCode(Response.Status.BAD_REQUEST,
                () -> client.getMetadataClient().assignSchemeToProject(LITTLE_SCHEMER_ID, 10020L).claim());
    }


}

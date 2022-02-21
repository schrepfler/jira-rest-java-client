/*
 * Copyright (C) 2012-2014 Atlassian
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
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.FieldType;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.OperationLink;
import com.atlassian.jira.rest.client.api.domain.Operations;
import com.atlassian.jira.rest.client.api.domain.Page;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.api.domain.Votes;
import com.atlassian.jira.rest.client.api.domain.Watchers;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.jira.rest.client.internal.json.TestConstants;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.stream.Collectors;

import static com.atlassian.jira.rest.client.IntegrationTestUtil.USER1;
import static com.atlassian.jira.rest.client.IntegrationTestUtil.getUserUri;
import static com.atlassian.jira.rest.client.TestUtil.assertErrorCode;
import static com.atlassian.jira.rest.client.api.IssueRestClient.Expandos.CHANGELOG;
import static com.atlassian.jira.rest.client.api.IssueRestClient.Expandos.OPERATIONS;
import static com.atlassian.jira.rest.client.internal.ServerVersionConstants.BN_JIRA_5;
import static com.atlassian.jira.rest.client.internal.ServerVersionConstants.BN_JIRA_8_4;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.toArray;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Those tests mustn't change anything on server side, as jira is restored only once
 */
@SuppressWarnings("ConstantConditions") // To ignore "May produce NPE" warnings
public class AsynchronousIssueRestClientReadOnlyTest extends AbstractAsynchronousRestClientTest {

    private static final String REMOVED_USER_NAME = "removed_user";
    private static final String ISSUE_KEY_WITH_REMOVED_USER_DATA = "ANONEDIT-1";

    // no timezone here, as JIRA does not store timezone information in its dump file
    private final DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime("2010-08-04T17:46:45.454");

    private static boolean alreadyRestored;

    @Before
    public void setup() {
        if (!alreadyRestored) {
            IntegrationTestUtil.restoreAppropriateJiraData(TestConstants.JIRA_DUMP_WITH_COMMENT_AND_WORKLOG_FROM_REMOVED_USER_FILE, administration);
            alreadyRestored = true;
        }
    }

    @Test
    public void testGetWatchers() throws Exception {
        final Issue issue = client.getIssueClient().getIssue("TST-1").claim();
        final Watchers watchers = client.getIssueClient().getWatchers(issue.getWatchers().getSelf()).claim();
        assertEquals(1, watchers.getNumWatchers());
        assertFalse(watchers.isWatching());
        assertThat(watchers.getUsers(), containsInAnyOrder(USER1));
    }

    @Test
    public void testGetWatcherForAnonymouslyAccessibleIssue() {
        setAnonymousMode();
        final Issue issue = client.getIssueClient().getIssue("ANNON-1").claim();
        final Watchers watchers = client.getIssueClient().getWatchers(issue.getWatchers().getSelf()).claim();
        assertEquals(1, watchers.getNumWatchers());
        assertFalse(watchers.isWatching());
        assertTrue("JRADEV-3594 bug!!!", Iterables.isEmpty(watchers.getUsers()));
        // to save time
        assertEquals(new TimeTracking(2700, 2400, null), issue.getTimeTracking());
    }

    @Test
    public void testGetIssueWithAnonymouslyCreatedAttachment() {
        setAnonymousMode();
        final Issue issue = client.getIssueClient().getIssue("ANONEDIT-1").claim();
        final Iterator<Attachment> attachmentIterator = issue.getAttachments().iterator();
        assertTrue(attachmentIterator.hasNext());
        assertNull(attachmentIterator.next().getAuthor());
    }

    @Test
    public void testGetIssueWithAnonymouslyCreatedWorklogEntry() {
        setAnonymousMode();
        final Issue issue = client.getIssueClient().getIssue("ANONEDIT-2").claim();
        final Iterator<Worklog> worklogIterator = issue.getWorklogs().iterator();
        assertTrue(worklogIterator.hasNext());
        assertNull(worklogIterator.next().getAuthor());
    }

    // URIs are broken in 5.0 - https://jdog.atlassian.com/browse/JRADEV-7691
    private void assertEqualsNoUri(BasicUser expected, BasicUser actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDisplayName(), actual.getDisplayName());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testGetIssue() throws Exception {
        final Issue issue = client.getIssueClient().getIssue("TST-1").claim();
        assertEquals("TST-1", issue.getKey());
        assertEquals(Long.valueOf(10000), issue.getId());
        assertTrue(issue.getSelf().toString().startsWith(jiraUri.toString()));
        assertEqualsNoUri(IntegrationTestUtil.USER_ADMIN, issue.getReporter());
        assertEqualsNoUri(IntegrationTestUtil.USER_ADMIN, issue.getAssignee());

        assertThat(issue.getLabels(), containsInAnyOrder("a", "bcds"));

        assertEquals(3, size(issue.getComments()));

        final String[] expandosForJira5 = {"renderedFields", "names", "schema", "transitions", "operations", "editmeta", "changelog"};
        final String[] expandosForJira6_4 = toArray(Lists.asList("versionedRepresentations", expandosForJira5), String.class);

        // here is anyOf matcher because "versionedRepresentations" was introduced in the middle of v6.4
        assertThat(issue.getExpandos(), anyOf(containsInAnyOrder(expandosForJira5), containsInAnyOrder(expandosForJira6_4)));
        assertEquals(new TimeTracking(null, 0, 190), issue.getTimeTracking());
        assertTrue(size(issue.getFields()) > 0);

        assertEquals(IntegrationTestUtil.START_PROGRESS_TRANSITION_ID, size(issue.getAttachments()));
        final Iterable<Attachment> items = issue.getAttachments();
        assertNotNull(items);
        Attachment attachment1 = new Attachment(IntegrationTestUtil.concat(
                IntegrationTestUtil.TESTING_JIRA_5_OR_NEWER ? UriBuilder.fromUri(jiraUri).path("/rest/api/2/").build()
                        : jiraRestRootUri, "/attachment/10040"),
                "dla Paw\u0142a.txt", IntegrationTestUtil.USER_ADMIN, dateTime, 643, "text/plain",
                IntegrationTestUtil.concat(jiraUri, "/secure/attachment/10040/dla+Paw%C5%82a.txt"), null);

        assertEquals(attachment1, items.iterator().next());

        // test for changelog
        assertNull(issue.getChangelog());

        final Issue issueWithChangelogAndOperations = client.getIssueClient().getIssue("TST-2", EnumSet.of(CHANGELOG, OPERATIONS))
                .claim();
        final Iterable<ChangelogGroup> changelog = issueWithChangelogAndOperations.getChangelog();
        if (isJira5xOrNewer()) {
            assertNotNull(changelog);
            final ChangelogGroup chg1 = Iterables.get(changelog, 18);
            assertEquals("admin", chg1.getAuthor().getName());
            assertEquals("Administrator", chg1.getAuthor().getDisplayName());
            assertEquals(new DateTime(2010, 8, 17, 16, 40, 34, 924).toInstant(), chg1.getCreated().toInstant());

            assertEquals(singletonList(new ChangelogItem(FieldType.JIRA, "status", "1", "Open", "3", "In Progress")), chg1
                    .getItems());

            final ChangelogGroup chg2 = Iterables.get(changelog, 20);
            assertEquals("admin", chg2.getAuthor().getName());
            assertEquals("Administrator", chg2.getAuthor().getDisplayName());
            assertEquals(new DateTime(2010, 8, 24, 16, 10, 23, 468).toInstant(), chg2.getCreated().toInstant());

            // there is not guarantee for the order of the items
            Matcher<Iterable<? extends ChangelogItem>> historyItemsMatcher = containsInAnyOrder(
                    new ChangelogItem(FieldType.JIRA, "timeoriginalestimate", null, null, "0", "0"),
                    new ChangelogItem(FieldType.CUSTOM, "My Radio buttons", null, null, null, "Another"),
                    new ChangelogItem(FieldType.CUSTOM, "project3", null, null, "10000", "Test Project"),
                    new ChangelogItem(FieldType.CUSTOM, "My Number Field New", null, null, null, "1.45")
            );
            assertThat(chg2.getItems(), historyItemsMatcher);
        }
        final Operations operations = issueWithChangelogAndOperations.getOperations();
        if (isJira5xOrNewer()) {
            assertThat(operations, notNullValue());
            assertThat(operations.getOperationById("log-work"), allOf(
                    instanceOf(OperationLink.class),
                    hasProperty("id", is("log-work"))
                    )
            );
        }
    }

    @Test
    public void testGetIssueWithNonTrivialComments() {
        final Issue issue = client.getIssueClient().getIssue("TST-2").claim();
        final Iterable<Comment> comments = issue.getComments();
        assertEquals(3, size(comments));
        final Comment c1 = Iterables.get(comments, 0);
        assertEquals(Visibility.role("Administrators"), c1.getVisibility());

        final Comment c3 = Iterables.get(comments, 2);
        assertEquals(Visibility.group("jira-users"), c3.getVisibility());

    }

    @Test
    public void testGetVoter() {
        final Issue issue = client.getIssueClient().getIssue("TST-1").claim();
        final Votes votes = client.getIssueClient().getVotes(issue.getVotes().getSelf()).claim();
        assertFalse(votes.hasVoted());
        assertThat(votes.getUsers(), containsInAnyOrder(USER1));
    }

    @Test
    public void testGetVotersWithoutViewIssuePermission() {
        final Issue issue = client.getIssueClient().getIssue("RST-1").claim();
        setUser2();
        final String optionalDot = isJira5xOrNewer() ? "." : "";
        assertErrorCode(Response.Status.FORBIDDEN,
                "You do not have the permission to see the specified issue" + optionalDot, new Runnable() {
                    @Override
                    public void run() {
                        client.getIssueClient().getVotes(issue.getVotes().getSelf()).claim();
                    }
                });
    }

    @Test
    public void testGetVotersWithoutViewVotersPermission() {
        setUser2();
        assertNumVotesAndNoVotersDetails("TST-1", 1);
    }

    @Test
    public void testGetVotersAnonymously() {
        setAnonymousMode();
        assertNumVotesAndNoVotersDetails("ANNON-1", 0);
    }


    private void assertNumVotesAndNoVotersDetails(final String issueKey, final int numVotes) {
        final Issue issue = client.getIssueClient().getIssue(issueKey).claim();
        assertEquals(numVotes, issue.getVotes().getVotes());
        assertFalse(issue.getVotes().hasVoted());
        final Votes votes = client.getIssueClient().getVotes(issue.getVotes().getSelf()).claim();
        assertFalse(votes.hasVoted());
        assertEquals(numVotes, votes.getVotes());
        assertTrue(Iterables.isEmpty(votes.getUsers()));
    }


    @Test
    public void testGetTransitions() throws Exception {
        final Issue issue = client.getIssueClient().getIssue("TST-1").claim();
        final Iterable<Transition> transitions = client.getIssueClient().getTransitions(issue).claim();
        assertEquals(4, size(transitions));
        assertTrue(Iterables
                .contains(transitions, new Transition("Start Progress", IntegrationTestUtil.START_PROGRESS_TRANSITION_ID, Collections
                        .<Transition.Field>emptyList())));
    }

    @JiraBuildNumberDependent(BN_JIRA_8_4)
    @Test
    public void testGetCreateIssueMetaProjectIssueTypes() throws Exception {
        final Page<IssueType> issueTypes = client.getIssueClient().getCreateIssueMetaProjectIssueTypes("ANONEDIT", null, null).claim();

        assertEquals(5, size(issueTypes.getValues()));
        assertEquals(0, issueTypes.getStartAt());
        assertEquals(50, issueTypes.getMaxResults());
        assertTrue(issueTypes.isLast());
        assertThat(Streams.stream(issueTypes.getValues()).map(x -> x.getName()).collect(Collectors.toList()),
                contains("Bug", "New Feature", "Task", "Improvement", "Sub-task"));
    }

    @JiraBuildNumberDependent(BN_JIRA_8_4)
    @Test
    public void testGetCreateIssueMetaFields() throws Exception {
        final Page<CimFieldInfo> fields = client.getIssueClient().getCreateIssueMetaFields("ANONEDIT", "1", null, null).claim();

        assertEquals(19, size(fields.getValues()));
        assertEquals(0, fields.getStartAt());
        assertEquals(50, fields.getMaxResults());
        assertTrue(fields.isLast());
        assertThat(Streams.stream(fields.getValues()).map(x -> x.getName()).collect(Collectors.toList()),
                allOf(hasItem("Summary")));
    }

    @Test
    public void testFetchingIssueWithWorklogWhenAuthorIsDeleted() {
        final Issue issue = client.getIssueClient().getIssue(ISSUE_KEY_WITH_REMOVED_USER_DATA).claim();
        final Worklog worklog = issue.getWorklogs().iterator().next();
        assertNotNull(worklog);
        final BasicUser author = worklog.getAuthor();
        assertNotNull(author);
        assertThat(author.getName(), equalTo(REMOVED_USER_NAME));
        assertTrue("expected incomplete self uri", author.isSelfUriIncomplete());
    }

    @Test
    public void testFetchingIssueWithCommentWhenAuthorIsDeleted() {
        final Issue issue = client.getIssueClient().getIssue(ISSUE_KEY_WITH_REMOVED_USER_DATA).claim();
        final Comment comment = issue.getComments().iterator().next();
        assertNotNull(comment);
        final BasicUser author = comment.getAuthor();
        assertNotNull(author);
        assertEquals(getUserUri(REMOVED_USER_NAME), author.getSelf());
    }
}

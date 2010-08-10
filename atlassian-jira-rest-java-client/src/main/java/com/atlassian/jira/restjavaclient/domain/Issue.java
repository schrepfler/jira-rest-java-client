package com.atlassian.jira.restjavaclient.domain;

import com.atlassian.jira.restjavaclient.AddressableEntity;
import com.atlassian.jira.restjavaclient.ExpandableProperty;
import com.atlassian.jira.restjavaclient.ExpandableResource;
import com.google.common.base.Objects;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Collection;

/**
 * TODO: Document this class / interface here
 *
 * @since v0.1
 */
public class Issue implements AddressableEntity, ExpandableResource {

    public Issue(URI self, String key, Project project, IssueType issueType, BasicStatus status, Iterable<String> expandos,
			ExpandableProperty<Comment> comments, ExpandableProperty<Attachment> attachments, Collection<Field> fields,
			DateTime creationDate, DateTime updateDate, URI transitionsUri, Collection<IssueLink> issueLinks, Votes votes, ExpandableProperty<Worklog> worklogs,
			Watchers watchers) {
        this.self = self;
        this.key = key;
		this.project = project;
		this.status = status;
		this.expandos = expandos;
        this.comments = comments;
        this.attachments = attachments;
		this.fields = fields;
		this.issueType = issueType;
		this.creationDate = creationDate;
		this.updateDate = updateDate;
		this.transitionsUri = transitionsUri;
		this.issueLinks = issueLinks;
		this.votes = votes;
		this.worklogs = worklogs;
		this.watchers = watchers;
	}

	private final BasicStatus status;
    private final URI self;
	private IssueType issueType;
	private Project project;
	private final URI transitionsUri;
	private final Iterable<String> expandos;
	private User reporter;
	private User assignee;
	private String key;
	private Collection<Field> fields;
	private DateTime creationDate;
	private DateTime updateDate;
	private final Votes votes;

	private final ExpandableProperty<Comment> comments;

	@Nullable
	private final Collection<IssueLink> issueLinks;

	private final ExpandableProperty<Attachment> attachments;

	private final ExpandableProperty<Worklog> worklogs;
	private final Watchers watchers;

	public BasicStatus getStatus() {
		return status;
	}

	public User getReporter() {
		return reporter;
	}

	public User getAssignee() {
		return assignee;
	}


	public String getSummary() {
		return null;
	}

	/**
	 *
	 * @return issue links for this issue (possibly nothing) or <code>null</code> when issue links are deactivated for this JIRA instance
	 */
	public Iterable<IssueLink> getIssueLinks() {
		return issueLinks;
	}

	public Iterable<Field> getFields() {
		return fields;
	}

	public String getKey() {
		return key;
	}

	public URI getSelf() {
		return self;
	}

	public Iterable<String> getExpandos() {
		return expandos;
	}

    public ExpandableProperty<Attachment> getAttachments() {
        return attachments;
    }

    public ExpandableProperty<Comment> getComments() {
        return comments;
    }

	public Project getProject() {
		return project;
	}

	public Votes getVotes() {
		return votes;
	}

	public ExpandableProperty<Worklog> getWorklogs() {
		return worklogs;
	}

	public Watchers getWatchers() {
		return watchers;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("self", self).
				add("key", key).
				add("project", project).
				add("status", status).
				add("expandos", expandos).
				add("reporter", reporter).
				add("assignee", assignee).addValue("\n").
				add("fields", fields).addValue("\n").
				add("issueType", issueType).
				add("creationDate", creationDate).
				add("updateDate", updateDate).addValue("\n").
				add("attachments", attachments).addValue("\n").
				add("comments", comments).addValue("\n").
				add("transitionsUri", transitionsUri).
				add("issueLinks", issueLinks).addValue("\n").
				add("votes", votes).addValue("\n").
				add("worklogs", worklogs).addValue("\n").
				add("watchers", watchers).
				toString();
	}
}

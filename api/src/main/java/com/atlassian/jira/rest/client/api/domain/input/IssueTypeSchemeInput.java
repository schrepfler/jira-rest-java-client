package com.atlassian.jira.rest.client.api.domain.input;

import java.util.List;

/**
 * TODO: Document this class / interface here
 *
 * @since v8.0
 */
public class IssueTypeSchemeInput {

    private final String name;
    private final String description;
    private final List<Long> issueTypeIds;

    private final Long defaultIssueTypeId;

    public IssueTypeSchemeInput(String name, String description, List<Long> issueTypeIds, Long defaultIssueTypeId) {
        this.name = name;
        this.description = description;
        this.defaultIssueTypeId = defaultIssueTypeId;
        this.issueTypeIds = issueTypeIds;
    }

    public IssueTypeSchemeInput(String name, String description, List<Long> issueTypeIds) {
        this(name, description, issueTypeIds, null);
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getIssueTypeIds() {
        return issueTypeIds;
    }

    public Long getDefaultIssueTypeId() {
        return defaultIssueTypeId;
    }
}

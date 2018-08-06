package com.atlassian.jira.rest.client.api.domain.input;

import java.util.List;

/**
 * Pojo representing the input parameters (i.e. body of a request) for the creation or update of an issue type scheme.
 *
 * @since 5.2
 */
public class IssueTypeSchemeInput {

    private final String name;
    private final String description;
    private final List<Long> issueTypeIds;

    private final Long defaultIssueTypeId;

    /**
     * Creates a new IsssueTypeSchemeInput with the specified name, description, issue type ids and default issue type id.
     * Note that a null default issue type id implies no default.
     * @param name
     * @param description
     * @param issueTypeIds
     * @param defaultIssueTypeId
     */
    public IssueTypeSchemeInput(String name, String description, List<Long> issueTypeIds, Long defaultIssueTypeId) {
        this.name = name;
        this.description = description;
        this.defaultIssueTypeId = defaultIssueTypeId;
        this.issueTypeIds = issueTypeIds;
    }

    /**
     * Creates a new IsssueTypeSchemeInput with the specified name, description, issue type ids and nothing specified for
     * the default issue type.
     *
     * @param name
     * @param description
     * @param issueTypeIds
     */
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

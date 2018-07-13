package com.atlassian.jira.rest.client.api.domain.input;

/**
 * TODO: Document this class / interface here
 *
 * @since v7.4
 */
public class IssueTypeSchemeInput {

    private final String name;
    private final String description;

    private final String defaultIssueType;

    public IssueTypeSchemeInput(String name, String description, String defaultIssueType) {
        this.name = name;
        this.description = description;
        this.defaultIssueType = defaultIssueType;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultIssueType() {
        return defaultIssueType;
    }

}

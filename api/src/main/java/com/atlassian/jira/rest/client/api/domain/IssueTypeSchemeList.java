package com.atlassian.jira.rest.client.api.domain;

import java.util.List;

/**
 * JRJC-side POJO that's a full mapping of a Collection of IssueTypeSchemes defined in Jira.
 * Broken out separately from a simlpe Iterable<IssueTypeScheme>to handle the extra 'expand' data that comes along for the ride.
 *
 * @since 5.2
 */
public class IssueTypeSchemeList {

    private final String expand;
    private final List<IssueTypeScheme> issueTypeSchemes;

    public IssueTypeSchemeList(String expand, List<IssueTypeScheme> issueTypeSchemes) {
        this.expand = expand;
        this.issueTypeSchemes = issueTypeSchemes;
    }


    @Override
    public String toString() {

        String schemes = issueTypeSchemes.stream()
                            .map(s -> s.toString())
                            .reduce("", (a,b) -> a + ",  " + b);

        return "IssueTypeSchemeList{" +
                "expand=" + expand +
                ", schemes= "  + schemes +
                '}';
    }

    public String getExpand() {
        return expand;
    }

    public List<IssueTypeScheme> getIssueTypeSchemes() {
        return issueTypeSchemes;
    }
}

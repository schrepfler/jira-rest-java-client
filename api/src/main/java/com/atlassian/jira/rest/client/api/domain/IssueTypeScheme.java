package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.AddressableEntity;
import com.atlassian.jira.rest.client.api.IdentifiableEntity;
import com.atlassian.jira.rest.client.api.NamedEntity;
import com.google.common.base.Objects;

import java.net.URI;

/**
 * TODO: Document this class / interface here
 *
 */
public class IssueTypeScheme implements AddressableEntity, NamedEntity, IdentifiableEntity<Long> {

    private final URI self;
    private final Long id;
    private final String name;
    private final String description;
    private final String defaultIssueType;

    public IssueTypeScheme(URI self, Long id, String name, String description, String defaultIssueType) {
        this.self = self;
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultIssueType = defaultIssueType;
    }


    // TODO : list of the issue types associated with the scheme
    //see @ bulk user edits & such for an example

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public String getDefaultIssueType() {
        return defaultIssueType;
    }

    public Long getId() {
        return id;
    }

    protected Objects.ToStringHelper getToStringHelper() {
        return Objects.toStringHelper(this)
                .add("self", self)
                .add("id", id)
                .add("name", name)
                .add("description",description)
                .add("defaultIssueType", defaultIssueType);
    }

    @Override
    public String toString() {
        return getToStringHelper().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IssueType) {
            IssueTypeScheme that = (IssueTypeScheme) obj;
            return Objects.equal(this.self, that.self)
                    && Objects.equal(this.id, that.id)
                    && Objects.equal(this.name, that.name)
                    && Objects.equal(this.description, that.description)
                    && Objects.equal(this.defaultIssueType, that.defaultIssueType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(self, id, name, description, defaultIssueType);
    }

    @Override
    public URI getSelf() { return self; }

}

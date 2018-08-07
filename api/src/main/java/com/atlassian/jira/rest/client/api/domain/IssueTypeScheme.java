package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.AddressableEntity;
import com.atlassian.jira.rest.client.api.IdentifiableEntity;
import com.atlassian.jira.rest.client.api.NamedEntity;
import com.google.common.base.Objects;

import java.net.URI;
import java.util.List;

/**
 * JRJC-side POJO that's a full mapping of an IssueTypeScheme defined in Jira.
 * @since 5.2
 */
public class IssueTypeScheme implements AddressableEntity, NamedEntity, IdentifiableEntity<Long> {

    @Override
    public String toString() {
        return "IssueTypeScheme{" +
                "self=" + self +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", defaultIssueType='" + defaultIssueType + '\'' +
                ", issueTypes=" + issueTypes +
                '}';
    }

    private final URI self;
    private final Long id;
    private final String name;
    private final String description;
    private final IssueType defaultIssueType;
    private final List<IssueType> issueTypes;

    public IssueTypeScheme(URI self, Long id, String name, String description, IssueType defaultIssueType, List<IssueType> issueTypes) {
        this.self = self;
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultIssueType = defaultIssueType;
        this.issueTypes = issueTypes;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public IssueType getDefaultIssueType() {
        return defaultIssueType;
    }

    public Long getId() {
        return id;
    }


    @Override
    public URI getSelf() { return self; }

    public List<IssueType> getIssueTypes() {
        return issueTypes;
    }


    protected Objects.ToStringHelper getToStringHelper() {
        return Objects.toStringHelper(this)
                .add("self", self)
                .add("id", id)
                .add("name", name)
                .add("description",description)
                .add("defaultIssueType", defaultIssueType)
                .add("issueTypes", issueTypes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IssueTypeScheme) {
            IssueTypeScheme that = (IssueTypeScheme) obj;
            return Objects.equal(this.self, that.self)
                    && Objects.equal(this.id, that.id)
                    && Objects.equal(this.name, that.name)
                    && Objects.equal(this.description, that.description)
                    && Objects.equal(this.defaultIssueType, that.defaultIssueType)
                    && Objects.equal(this.issueTypes, that.issueTypes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(self, id, name, description, defaultIssueType, issueTypes);
    }

}

package com.atlassian.jira.rest.client.api.domain.input;

/**
 * Pojo representing either a unique id OR key for a project within a Jira system.
 *
 * @since 5.2
 */
public class ProjectIdOrKey {

    private final String idOrKey;


    public ProjectIdOrKey(String idOrKey) {
        this.idOrKey = idOrKey;
    }


    public String getIdOrKey() {
        return idOrKey;
    }
}

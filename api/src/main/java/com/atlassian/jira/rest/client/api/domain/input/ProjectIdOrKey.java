package com.atlassian.jira.rest.client.api.domain.input;

/**
 * TODO: Document this class / interface here
 *
 * @since v7.4
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

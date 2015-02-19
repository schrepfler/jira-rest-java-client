package com.atlassian.jira.rest.client.internal.jersey;

import com.atlassian.jira.rest.client.AuthenticationHandler;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
* Created by klopacinski on 2015-02-12.
*/
public class JerseyJiraRestClientBuilder implements JiraRestClientFactory {

    private ImmutableMap<String, String> headers;
    private ImmutableMap<String, String> queryParams;

    public JerseyJiraRestClientBuilder() {
        headers = ImmutableMap.of();
        queryParams = ImmutableMap.of();
    }

    private JerseyJiraRestClientBuilder(ImmutableMap<String, String> headers, ImmutableMap<String, String> queryParams) {
        this.headers = headers;
        this.queryParams = queryParams;
    }

    @Override
    public JiraRestClient create(URI serverUri, AuthenticationHandler authenticationHandler) {
        return new JerseyJiraRestClientFactory().create(serverUri, authenticationHandler, false, headers, queryParams);
    }

    @Override
    public JiraRestClient create(URI serverUri, AuthenticationHandler authenticationHandler, boolean followRedirects) {
        return new JerseyJiraRestClientFactory().create(serverUri, authenticationHandler, followRedirects, headers, queryParams);
    }

    @Override
    public JiraRestClient createWithBasicHttpAuthentication(URI serverUri, String username, String password) {
        return create(serverUri, new BasicHttpAuthenticationHandler(username, password));
    }

    /**
     * An HTTP header that will be appended to each request made by this client.
     * @param header
     * @param value
     * @return
     */
    public JerseyJiraRestClientBuilder header(String header, String value) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        mapBuilder.putAll(headers);
        mapBuilder.put(header, value);
        return new JerseyJiraRestClientBuilder(mapBuilder.build(), queryParams);
    };

    /**
     * A query parameter that will be appended to each request made by this client.
     * @param key
     * @param value
     * @return
     */
    public JerseyJiraRestClientBuilder queryParam(String key, String value) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        mapBuilder.putAll(queryParams);
        mapBuilder.put(key, value);
        return new JerseyJiraRestClientBuilder(headers, mapBuilder.build());
    }
}

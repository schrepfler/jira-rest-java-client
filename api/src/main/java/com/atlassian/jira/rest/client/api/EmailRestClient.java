package com.atlassian.jira.rest.client.api;

import com.atlassian.httpclient.api.Response;
import io.atlassian.util.concurrent.Promise;

public interface EmailRestClient {

    Promise<Response> postMessage(String destAddr, String subject, String body);
}

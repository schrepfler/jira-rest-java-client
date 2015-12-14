package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.httpclient.api.HttpClient;

import java.net.URL;
import java.net.URLConnection;

/**
 * Atlassian HttpClient with destroy exposed.
 */
public interface DisposableHttpClient extends HttpClient {

	abstract void destroy() throws Exception;

}

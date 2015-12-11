package com.atlassian.jira.rest.client.internal.async;

import java.net.URL;
import java.net.URLConnection;

/**
 * Atlassian HttpClient with destroy exposed.
 */
public abstract class DisposableHttpClient extends URLConnection {

	DisposableHttpClient(URL url) {
		super(url);
	}

	abstract void destroy() throws Exception;

}

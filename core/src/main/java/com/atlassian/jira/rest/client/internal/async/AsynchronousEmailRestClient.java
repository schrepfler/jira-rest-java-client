package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Response;
import com.atlassian.jira.rest.client.api.EmailRestClient;
import com.atlassian.util.concurrent.Promise;
import com.google.common.annotations.VisibleForTesting;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by kpratt on 12/15/16.
 */
public class AsynchronousEmailRestClient implements EmailRestClient {

    final URI baseUri;
    final HttpClient client;
    final String authToken;

    public AsynchronousEmailRestClient(final URI baseUri, final HttpClient client) {
        this.client = client;
        this.baseUri = baseUri;
        this.authToken = "A";
    }

    @VisibleForTesting
    public static String constructEmail(String destEmail, String subject, String body) {
        StringBuilder buff = new StringBuilder();
        buff.append(
                "Received: from smtp-out.example.com (123.45.67.89) by\n" +
                        "in.example.com (87.65.43.210); Wed, 2 Mar 2011 11:39:39 -0800\n" +
                        "From: \"test\" <test@example.com>\n" +
                        "To: " + destEmail + "\n" +
                        "Subject: " + subject + "\n" +
                        "Message-ID: <97DCB304-C529-4779-BEBC-FC8357FCC4D2@example.com>\n" +
                        "Accept-Language: en-US\n" +
                        "Content-Language: en-US\n" +
                        "MIME-Version: 1.0\n" +
                        "Content-Type: text/plain; charset=\"us-ascii\"\n" +
                        "\n" +
                        body
        );
        return buff.toString();
    }

    public Promise<Response> postMessage(String destEmail, String subject, String body) {
        final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("email").path("incoming")
                .queryParam("email", destEmail);

        return client.newRequest(uriBuilder.build())
                .setContentType("text/plain")
                .setEntity(constructEmail(destEmail, subject, body))
                .post();
    }

}

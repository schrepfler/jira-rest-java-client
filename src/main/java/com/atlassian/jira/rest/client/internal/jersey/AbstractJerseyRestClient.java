/*
 * Copyright (C) 2010 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.jersey;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.*;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.sun.jersey.api.client.ClientResponse.Status.MOVED_PERMANENTLY;
import static com.sun.jersey.api.client.ClientResponse.Status.TEMPORARY_REDIRECT;

/**
 * Parent class for Jersey-based implementation of REST clients
 *
 * @since v0.1
 */
public abstract class AbstractJerseyRestClient {
    public static final int MAX_REDIRECTS = 10;
    protected final ApacheHttpClient client;
    private final boolean followRedirects;
    protected final URI baseUri;

    private Map<String, String> headers;
    private Map<String, String> queryParams;

    public AbstractJerseyRestClient(URI baseUri, ApacheHttpClient client, boolean followRedirects) {
        this.baseUri = baseUri;
        this.client = client;
        this.followRedirects = followRedirects;
        this.headers = ImmutableMap.of();
        this.queryParams = ImmutableMap.of();
    }

    protected <T> T invoke(Callable<T> callable) throws RestClientException {
        try {
            return callable.call();
        } catch (UniformInterfaceException e) {
//			// maybe captcha?
//			if (e.getResponse().getClientResponseStatus() == ClientResponse.Status.FORBIDDEN || e.getResponse().getClientResponseStatus() == ClientResponse.Status.UNAUTHORIZED) {
//
//			}
//			final List<String> headers = e.getResponse().getHeaders().get("X-Authentication-Denied-Reason");
//			if (headers != null) {
//				System.out.println(headers);
//			}
//
            try {
                final String body = e.getResponse().getEntity(String.class);
                final Collection<String> errorMessages = extractErrors(body);
                throw new RestClientException(errorMessages, e);
            } catch (JSONException e1) {
                throw new RestClientException(e);
            }
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RestClientException(e);
        }
    }

    private WebResource.Builder createWebResource(URI uri) {
        WebResource webResource = client.resource(uri);
        for (Map.Entry<String, String> queryParam: queryParams.entrySet()) {
            webResource = webResource.queryParam(queryParam.getKey(), queryParam.getValue());
        }
        WebResource.Builder webResourceBuilder = webResource.getRequestBuilder();
        for (Map.Entry<String, String> header: headers.entrySet()) {
            webResourceBuilder = webResourceBuilder.header(header.getKey(), header.getValue());
        }
        return webResourceBuilder;
    }

    protected <T> T getAndParse(final URI uri, final JsonObjectParser<T> parser, ProgressMonitor progressMonitor) {
        return invoke(new Callable<T>() {
            @Override
            public T call() throws Exception {
                WebResource.Builder webResource = createWebResource(uri);
                final JSONObject s = webResource.get(JSONObject.class);
                return parser.parse(s);
            }
        });

    }

    protected <T> T getAndParse(final URI uri, final JsonArrayParser<T> parser, ProgressMonitor progressMonitor) {
        return invoke(new Callable<T>() {
            @Override
            public T call() throws Exception {
                WebResource.Builder webResource = createWebResource(uri);
                final JSONArray jsonArray = webResource.get(JSONArray.class);
                return parser.parse(jsonArray);
            }
        });
    }

    protected void delete(final URI uri, ProgressMonitor progressMonitor) {
        invoke(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                WebResource.Builder webResource = createWebResource(uri);
                webResource.delete();
                return null;
            }
        });
    }

    protected <T> T postAndParse(final URI uri, @Nullable final JSONObject postEntity, final JsonObjectParser<T> parser, ProgressMonitor progressMonitor) {
        return invoke(new Callable<T>() {
            @Override
            public T call() throws Exception {
                WebResource.Builder webResource = createWebResource(uri);
                final JSONObject s = postEntity != null ? webResource.post(JSONObject.class, postEntity) : webResource.post(JSONObject.class);
                return parser.parse(s);
            }
        });
    }

    protected void post(final URI uri, @Nullable final JSONObject postEntity, ProgressMonitor progressMonitor) {
        post(uri, new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                return postEntity;

            }
        }, progressMonitor);
    }

    protected void post(final URI uri, final Callable<JSONObject> callable, ProgressMonitor progressMonitor) {
        invoke(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                WebResource.Builder webResource = createWebResource(uri);
                final JSONObject postEntity = callable.call();
                if (postEntity != null) {
                    webResource.post(postEntity);
                } else {
                    webResource.post();
                }
                return null;
            }
        });

    }

    protected <T> T postAndParse(final URI uri, final Callable<JSONObject> callable, final JsonObjectParser<T> parser, ProgressMonitor progressMonitor) {
        return impl(uri, Method.POST, callable, parser);
    }

    protected <T> T putAndParse(final URI uri, final Callable<JSONObject> callable, final JsonObjectParser<T> parser, ProgressMonitor progressMonitor) {
        return impl(uri, Method.PUT, callable, parser);
    }

    enum Method {
        PUT, POST
    }

    private <T> T impl(final URI uri, final Method method, final Callable<JSONObject> callable, final JsonObjectParser<T> parser) {
        return invoke(new Callable<T>() {
            @Override
            public T call() throws Exception {

                URI currentURI = uri;

                int i = 0;

                do {
                    try {
                        WebResource.Builder webResource = createWebResource(uri);
                        final JSONObject postEntity = callable.call();
                        final JSONObject s;
                        s = doHttpMethod(webResource, postEntity, method);
                        return parser.parse(s);
                    } catch (UniformInterfaceException e) {
                        if (followRedirects && i < MAX_REDIRECTS
                                && (e.getResponse().getStatus() == MOVED_PERMANENTLY.getStatusCode() || e.getResponse().getStatus() == TEMPORARY_REDIRECT.getStatusCode())) {
                            currentURI = e.getResponse().getLocation();
                        } else {
                            throw e;
                        }
                    } finally {
                        ++i;
                    }
                } while (i < MAX_REDIRECTS);

                throw new RestClientException("Maximum number of redirects reached.");
            }
        });
    }

    private JSONObject doHttpMethod(WebResource.Builder webResource, @Nullable JSONObject postEntity, Method method) {
        if (postEntity != null) {
            if (method == Method.POST) {
                return webResource.post(JSONObject.class, postEntity);
            } else {
                return webResource.put(JSONObject.class, postEntity);
            }
        } else {
            if (method == Method.POST) {
                return webResource.post(JSONObject.class);
            } else {
                return webResource.put(JSONObject.class);
            }
        }
    }


    static Collection<String> extractErrors(String body) throws JSONException {
        JSONObject jsonObject = new JSONObject(body);
        final Collection<String> errorMessages = new ArrayList<String>();
        final JSONArray errorMessagesJsonArray = jsonObject.optJSONArray("errorMessages");
        if (errorMessagesJsonArray != null) {
            errorMessages.addAll(JsonParseUtil.toStringCollection(errorMessagesJsonArray));
        }
        final JSONObject errorJsonObject = jsonObject.optJSONObject("errors");
        if (errorJsonObject != null) {
            final JSONArray valuesJsonArray = errorJsonObject.toJSONArray(errorJsonObject.names());
            if (valuesJsonArray != null) {
                errorMessages.addAll(JsonParseUtil.toStringCollection(valuesJsonArray));
            }
        }
        return errorMessages;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = ImmutableMap.copyOf(headers);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = ImmutableMap.copyOf(queryParams);
    }

    protected static class InputGeneratorCallable<T> implements Callable<JSONObject> {

        private final JsonGenerator<T> generator;
        private final T bean;

        public static <T> InputGeneratorCallable<T> create(JsonGenerator<T> generator, T bean) {
            return new InputGeneratorCallable<T>(generator, bean);
        }

        public InputGeneratorCallable(JsonGenerator<T> generator, T bean) {
            this.generator = generator;
            this.bean = bean;
        }

        @Override
        public JSONObject call() throws Exception {
            return generator.generate(bean);
        }
    }



}

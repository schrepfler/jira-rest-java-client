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

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.Transition;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TransitionParser implements JsonObjectParser<Iterable<Transition>> {

	private final TransitionJsonParser transitionJsonParser = new TransitionJsonParser();
	private final JsonObjectParser<Transition> transitionJsonParserV5 = new TransitionJsonParserV5();

	@Override
	public Iterable<Transition> parse(JSONObject json) throws JSONException {
		if (json.has("transitions")) {
			return JsonParseUtil.parseJsonArray(json.getJSONArray("transitions"), transitionJsonParserV5);
		} else {
			final Collection<Transition> transitions = new ArrayList<Transition>(json.length());
			@SuppressWarnings("unchecked")
			final Iterator<String> iterator = json.keys();
			while (iterator.hasNext()) {
				final String key = iterator.next();
				try {
					final int id = Integer.parseInt(key);
					final Transition transition = transitionJsonParser.parse(json.getJSONObject(key), id);
					transitions.add(transition);
				} catch (JSONException e) {
					throw new RestClientException(e);
				} catch (NumberFormatException e) {
					throw new RestClientException("Transition id should be an integer, but found [" + key + "]", e);
				}
			}
			return transitions;
		}
	}
}

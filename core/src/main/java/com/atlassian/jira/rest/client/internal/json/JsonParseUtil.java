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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.ExpandableProperty;
import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JsonParseUtil {
    public static final String JIRA_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final DateTimeFormatter JIRA_DATE_TIME_FORMATTER = DateTimeFormat.forPattern(JIRA_DATE_TIME_PATTERN);
    public static final DateTimeFormatter JIRA_DATE_FORMATTER = ISODateTimeFormat.date();
    public static final String SELF_ATTR = "self";

    public static <T> Collection<T> parseJsonArray(final JsonArray jsonArray, final JsonElementParser<T> jsonParser)
            throws JsonParseException {
        final Collection<T> res = new ArrayList<T>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            res.add(jsonParser.parse(jsonArray.get(i).getAsJsonObject()));
        }
        return res;
    }

    public static <T> OptionalIterable<T> parseOptionalJsonArray(final JsonArray jsonArray, final JsonElementParser<T> jsonParser)
            throws JsonParseException {
        if (jsonArray == null) {
            return OptionalIterable.absent();
        } else {
            return new OptionalIterable<T>(JsonParseUtil.parseJsonArray(jsonArray, jsonParser));
        }
    }

    public static <T> T parseOptionalJsonObject(final JsonObject json, final String attributeName, final JsonElementParser<T> jsonParser)
            throws JsonParseException {
        JsonObject attributeObject = getOptionalJsonObject(json, attributeName);
        return attributeObject != null ? jsonParser.parse(attributeObject) : null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static <T> ExpandableProperty<T> parseExpandableProperty(final JsonObject json, final JsonElementParser<T> expandablePropertyBuilder)
            throws JsonParseException {
        return parseExpandableProperty(json, false, expandablePropertyBuilder);
    }

    @Nullable
    public static <T> ExpandableProperty<T> parseOptionalExpandableProperty(@Nullable final JsonObject json, final JsonElementParser<T> expandablePropertyBuilder)
            throws JsonParseException {
        return parseExpandableProperty(json, true, expandablePropertyBuilder);
    }

    @Nullable
    private static <T> ExpandableProperty<T> parseExpandableProperty(@Nullable final JsonObject json, final Boolean optional, final JsonElementParser<T> expandablePropertyBuilder)
            throws JsonParseException {
        if (json == null) {
            if (!optional) {
                throw new IllegalArgumentException("json object cannot be null while optional is false");
            }
            return null;
        }

        final int numItems = JsonParseUtil.getAsInt(json, "size");
        final Collection<T> items;
        JsonArray itemsJa = json.get("items").getAsJsonArray();

        if (itemsJa.size() > 0) {
            items = new ArrayList<T>(numItems);
            for (int i = 0; i < itemsJa.size(); i++) {
                final T item = expandablePropertyBuilder.parse(itemsJa.get(i).getAsJsonObject());
                items.add(item);
            }
        } else {
            items = null;
        }

        return new ExpandableProperty<T>(numItems, items);
    }


    public static URI getSelfUri(final JsonObject jsonObject) throws JsonParseException {
        return parseURI(JsonParseUtil.getAsString(jsonObject, SELF_ATTR));
    }

    public static URI optSelfUri(final JsonObject jsonObject, final URI defaultUri) throws JsonParseException {
        final String selfUri = JsonParseUtil.getOptionalString(jsonObject, SELF_ATTR);
        return selfUri != null ? parseURI(selfUri) : defaultUri;
    }

    @SuppressWarnings("unused")
    public static JsonElement getNestedObject(JsonObject json, final String... path) throws JsonParseException {
        for (String s : path) {
            json = json.get(s).getAsJsonObject();
        }
        return json;
    }

    @Nullable
    public static JsonElement getNestedOptionalObject(JsonObject json, final String... path) throws JsonParseException {
        for (int i = 0; i < path.length - 1; i++) {
            if (json == null) {
                return null;
            }
            String s = path[i];
            json = json.getAsJsonObject(s);
        }
        return json == null? null : json.get(path[path.length - 1]);
    }

    @SuppressWarnings("unused")
    public static JsonArray getNestedArray(JsonObject json, final String... path) throws JsonParseException {
        for (int i = 0; i < path.length - 1; i++) {
            String s = path[i];
            json = json.get(s).getAsJsonObject();
        }
        return json.getAsJsonArray(path[path.length - 1]);
    }

    public static JsonArray getNestedOptionalArray(JsonObject json, final String... path) throws JsonParseException {
        for (int i = 0; json != null && i < path.length - 1; i++) {
            String s = path[i];
            if (json == null || !json.isJsonObject() || (json.has(s) && !json.get(s).isJsonObject())) {
                return null;
            }
            json = json.getAsJsonObject(s);
            if (json == null || json.isJsonNull()) {
                return null;
            }
        }
        return json.getAsJsonArray(path[path.length - 1]);
    }


    public static String getNestedString(JsonObject json, final String... path) throws JsonParseException {
        for (int i = 0; i < path.length - 1; i++) {
            String s = path[i];
            json = json.get(s).getAsJsonObject();
        }
        return getAsString(json, path[path.length - 1]);
    }

    @SuppressWarnings("unused")
    public static boolean getNestedBoolean(JsonObject json, final String... path) throws JsonParseException {
        for (int i = 0; i < path.length - 1; i++) {
            String s = path[i];
            json = json.get(s).getAsJsonObject();
        }
        return json.get(path[path.length - 1]).getAsBoolean();
    }


    public static URI parseURI(final String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            throw new RestClientException(e);
        }
    }

    @Nullable
    public static URI parseOptionalURI(final JsonObject jsonObject, final String attributeName) {
        final String s = getOptionalString(jsonObject, attributeName);
        return s != null ? parseURI(s) : null;
    }

    @Nullable
    public static BasicUser parseBasicUser(@Nullable final JsonObject json) throws JsonParseException {
        if (json == null) {
            return null;
        }
        final String username = JsonParseUtil.getAsString(json, "name");
        if (!json.has(JsonParseUtil.SELF_ATTR) && "Anonymous".equals(username)) {
            return null; // insane representation for unassigned user - JRADEV-4262
        }

        // deleted user? BUG in REST API: JRA-30263
        final URI selfUri = optSelfUri(json, BasicUser.INCOMPLETE_URI);
        return new BasicUser(selfUri, username, JsonParseUtil.getOptionalString(json, "displayName"));
    }

    public static DateTime parseDateTime(final JsonObject jsonObject, final String attributeName) throws JsonParseException {
        return parseDateTime(jsonObject.get(attributeName).getAsString());
    }

    @Nullable
    public static DateTime parseOptionalDateTime(final JsonObject jsonObject, final String attributeName) throws JsonParseException {
        final String s = getOptionalString(jsonObject, attributeName);
        return s != null ? parseDateTime(s) : null;
    }

    public static DateTime parseDateTime(final String str) {
        try {
            return JIRA_DATE_TIME_FORMATTER.parseDateTime(str);
        } catch (Exception e) {
            throw new RestClientException(e);
        }
    }

    /**
     * Tries to parse date and time and return that. If fails then tries to parse date only.
     *
     * @param str String contains either date and time or date only
     * @return date and time or date only
     */
    public static DateTime parseDateTimeOrDate(final String str) {
        try {
            return JIRA_DATE_TIME_FORMATTER.parseDateTime(str);
        } catch (Exception ignored) {
            try {
                return JIRA_DATE_FORMATTER.parseDateTime(str);
            } catch (Exception e) {
                throw new RestClientException(e);
            }
        }
    }

    public static DateTime parseDate(final String str) {
        try {
            return JIRA_DATE_FORMATTER.parseDateTime(str);
        } catch (Exception e) {
            throw new RestClientException(e);
        }
    }

    public static String formatDate(final DateTime dateTime) {
        return JIRA_DATE_FORMATTER.print(dateTime);
    }

    @SuppressWarnings("unused")
    public static String formatDateTime(final DateTime dateTime) {
        return JIRA_DATE_TIME_FORMATTER.print(dateTime);
    }


    @Nullable
    public static String getNullableString(final JsonObject jsonObject, final String attributeName) throws JsonParseException {
        final JsonElement o = jsonObject.get(attributeName);
        if (o == null || o.isJsonNull()) {
            return null;
        }
        return o.getAsString();
    }


    @Nullable
    public static String getOptionalString(final JsonObject jsonObject, final String attributeName) {
        if (jsonObject == null) {
            return null;
        }
        final JsonElement res = jsonObject.get(attributeName);
        if (res == null || res.isJsonNull()) {
            return null;
        }
        return res.getAsString();
    }

    @Nullable
    public static <T> T getOptionalJsonObject(final JsonObject jsonObject, final String attributeName, final JsonElementParser<T> jsonParser) throws JsonParseException {
        final JsonObject res = jsonObject.getAsJsonObject(attributeName);
        if (res == null || res.isJsonNull()) {
            return null;
        }
        return jsonParser.parse(res);
    }

    @SuppressWarnings("unused")
    @Nullable
    public static JsonObject getOptionalJsonObject(final JsonObject jsonObject, final String attributeName) {
        final JsonObject res = jsonObject.getAsJsonObject(attributeName);
        if (res == null || res.isJsonNull()) {
            return null;
        }
        return res;
    }


    public static Collection<String> toStringCollection(final JsonArray jsonArray) throws JsonParseException {
        final ArrayList<String> res = new ArrayList<String>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            res.add(jsonArray.get(i).getAsString());
        }
        return res;
    }

    public static Integer parseOptionInteger(final JsonObject json, final String attributeName) throws JsonParseException {
        return json.has(attributeName) ? JsonParseUtil.getAsInt(json, attributeName) : null;
    }

    @Nullable
    public static Long getOptionalLong(final JsonObject jsonObject, final String attributeName) throws JsonParseException {
        return jsonObject.has(attributeName) ? jsonObject.get(attributeName).getAsLong() : null;
    }

    public static Optional<JsonArray> getOptionalArray(final JsonObject jsonObject, final String attributeName)
            throws JsonParseException {
        return jsonObject.has(attributeName) ?
                Optional.of(jsonObject.get(attributeName).getAsJsonArray()) : Optional.<JsonArray>absent();
    }

    public static Map<String, URI> getAvatarUris(final JsonObject jsonObject) throws JsonParseException {
        Map<String, URI> uris = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (!(entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString())) {
                throw new JsonParseException("Cannot parse URIs: key is expected to be valid String. Got " +
                        (entry.getValue() == null ? "null" : entry.getValue().getClass()) + " instead.");
            }
            uris.put(entry.getKey(), JsonParseUtil.parseURI(entry.getValue().getAsString()));
        }
        return uris;
    }

    @SuppressWarnings("unchecked")
    public static Iterator<String> getStringKeys(final JsonObject json) {
        Set<String> keys = new TreeSet<String>();
        if (json != null) {
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                keys.add(entry.getKey());
            }
        }
        return keys.iterator();
    }

    public static Map<String, String> toStringMap(final JsonArray names, final JsonObject values) throws JsonParseException {
        final Map<String, String> result = Maps.newHashMap();
        for (int i = 0; i < names.size(); i++) {
            final String key = names.get(i).getAsString();
            result.put(key, values.get(key).getAsString());
        }
        return result;
    }

    public static Map<String, String> toStringMap(final JsonObject values) throws JsonParseException {
        final Map<String, String> result = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getAsString());
        }
        return result;
    }

    public static String getAsString(JsonObject obj, String fieldName) throws JsonParseException {
        JsonElement res = obj.get(fieldName);
        if (res == null) {
            // JSONObject[\"self\"] not found.
            throw new JsonParseException("JSONObject[\"" + fieldName + "\"] not found.");
        }
        return res.getAsString();
    }

    public static boolean getAsBoolean(JsonObject obj, String fieldName) throws JsonParseException {
        JsonElement res = obj.get(fieldName);
        if (res == null) {
            throw new JsonParseException("JSONObject[\"" + fieldName + "\"] not found.");
        }
        return res.getAsBoolean();
    }

    public static int getAsInt(JsonObject obj, String fieldName) throws JsonParseException {
        JsonElement res = obj.get(fieldName);
        if (res == null) {
            throw new JsonParseException("JSONObject[\"" + fieldName + "\"] not found.");
        }
        try {
            return res.getAsInt();
        } catch (ClassCastException | NumberFormatException e) {
            throw new JsonParseException("JSONObject[\"" + fieldName + "\"] is not a number.");
        }
    }
}
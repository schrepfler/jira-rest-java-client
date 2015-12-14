package com.atlassian.jira.rest.client.internal.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

/**
 * Created by aradu on 11/12/15.
 */
public class CustomFieldJsonParser implements JsonElementParser<Object> {
    @Override
    public Object parse(JsonElement jsonElement) throws JsonParseException {
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive asPriminive = jsonElement.getAsJsonPrimitive();
            try {
                return asPriminive.getAsDouble();
            } catch (NumberFormatException e) {
            }
            try {
                return asPriminive.getAsInt();
            } catch (NumberFormatException e) {
            }
            if (asPriminive.isNumber()) {
                return asPriminive.getAsNumber();
            } else if (asPriminive.isString()) {
                return asPriminive.getAsString();
            } else if (asPriminive.isBoolean()) {
                return asPriminive.getAsBoolean();
            }
        }
        return jsonElement;
    }
}

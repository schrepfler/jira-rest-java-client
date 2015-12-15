package com.atlassian.jira.rest.client.internal.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class CustomFieldJsonParser implements JsonElementParser<Object> {
    @Override
    public Object parse(JsonElement jsonElement) throws JsonParseException {
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive asPrimitive = jsonElement.getAsJsonPrimitive();
            // String needs to be returned first, since .getAs... will try to change the string to whatever type if it can.
            if (asPrimitive.isString()) {
                return asPrimitive.getAsString();
            }
            if (asPrimitive.isBoolean()) {
                return asPrimitive.getAsBoolean();
            }
            if (asPrimitive.isNumber()) {
                // Get as Number returns com.google.gson.internal.LazilyParserNumber, so the logic for separating it into
                // an integer or double is up to us. This is because JSON has no formal difference between integer
                // and double, but only has one numeric type.
                if (asPrimitive.getAsString().contains(".")) {
                    return asPrimitive.getAsDouble();
                } else {
                    return asPrimitive.getAsInt();
                }
            }
        }
        return jsonElement;
    }
}

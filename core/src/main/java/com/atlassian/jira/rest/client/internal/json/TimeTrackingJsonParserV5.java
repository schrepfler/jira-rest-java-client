/*
 * Copyright (C) 2011 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class TimeTrackingJsonParserV5 implements JsonElementParser<TimeTracking> {
    @Override
    public TimeTracking parse(JsonElement jsonElement) throws JsonParseException {
        final JsonObject json = jsonElement.getAsJsonObject();

        final Integer originalEstimateMinutes = JsonParseUtil.parseOptionInteger(json, "originalEstimateSeconds");
        final Integer timeRemainingMinutes = JsonParseUtil.parseOptionInteger(json, "remainingEstimateSeconds");
        final Integer timeSpentMinutes = JsonParseUtil.parseOptionInteger(json, "timeSpentSeconds");
        return new TimeTracking(originalEstimateMinutes != null ? originalEstimateMinutes / 60 : null,
                timeRemainingMinutes != null ? timeRemainingMinutes / 60 : null,
                timeSpentMinutes != null ? timeSpentMinutes / 60 : null);
    }

}

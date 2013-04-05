package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.domain.FavouriteFilter;
import com.google.common.collect.Lists;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: kalamon
 * Date: 04.04.13
 * Time: 14:49
 */
public class FavouriteFilterJsonParserTest {
    @Test
    public void testPl2818() {
        GenericJsonArrayParser<FavouriteFilter> favouriteFiltersJsonParser = GenericJsonArrayParser.create(new FavouriteFilterJsonParser());
        try {
            JSONArray array = ResourceUtil.getJsonArrayFromResource("/json/filter/pl-2818.json");
            Iterable<FavouriteFilter> filters = favouriteFiltersJsonParser.parse(array);
            Assert.assertEquals(Lists.newArrayList(filters).size(), 10);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
        }
    }
}

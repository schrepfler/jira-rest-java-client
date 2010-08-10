package com.atlassian.jira.restjavaclient.json;

import com.atlassian.jira.restjavaclient.ExpandableProperty;
import com.atlassian.jira.restjavaclient.RestClientException;
import com.atlassian.jira.restjavaclient.domain.User;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

public class JsonParseUtil {
	private static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

	public interface ExpandablePropertyBuilder<T> {
		T parse(JSONObject json) throws JSONException;
	}

	public static <T> ExpandableProperty<T> parseExpandableProperty(JSONObject json, ExpandablePropertyBuilder<T> expandablePropertyBuilder)
			throws JSONException {
		final int numItems = json.getInt("size");
		final Collection<T> items;
		JSONArray itemsJa = json.getJSONArray("items");

		if (itemsJa.length() > 0) {
			items = new ArrayList<T>(numItems);
			for (int i = 0; i < itemsJa.length(); i++) {
				final T item = expandablePropertyBuilder.parse(itemsJa.getJSONObject(i));
				items.add(item);
			}
		} else {
			items = null;
		}

		return new ExpandableProperty<T>(numItems, items);
	}



	public static URI getSelfUri(JSONObject jsonObject) throws JSONException {
		return parseURI(jsonObject.getString("self"));
	}

	public static JSONObject getNestedObject(JSONObject json, String... path) throws JSONException {
		for (String s : path) {
			json = json.getJSONObject(s);
		}
		return json;
	}

	public static String getNestedString(JSONObject json, String... path) throws JSONException {

		for (int i = 0; i < path.length - 1; i++) {
			String s = path[i];
			json = json.getJSONObject(s);
		}
		return json.getString(path[path.length - 1]);
	}
	

	public static URI parseURI(String str) {
		try {
			return new URI(str);
		} catch (URISyntaxException e) {
			throw new RestClientException(e);
		}
	}

	public static User parseUser(JSONObject json) throws JSONException {
		return new User(getSelfUri(json), json.getString("name"), json.optString("displayName", null));
	}

	public static DateTime parseDateTime(String str) {
		try {
			return DATE_TIME_FORMATTER.parseDateTime(str);
		} catch (Exception e) {
			throw new RestClientException(e);
		}
	}
}
package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class GoogleImageSearchResponseData implements Entity {

	private List<GoogleImageSearchResponseResult> results;

	public List<GoogleImageSearchResponseResult> getResults() {
		if (results == null) {
			return Collections.emptyList();
		}
		return results;
	}

	public void setResults(List<GoogleImageSearchResponseResult> results) {
		this.results = results;
	}

	public GoogleImageSearchResponseData load(JSONObject object) throws JSONException {

		JSONArray results = object.optJSONArray("results");
		if (results != null) {
			List<GoogleImageSearchResponseResult> resultsCollection = new ArrayList<GoogleImageSearchResponseResult>(
					results.length());
			for (int i = 0; i < results.length(); i++) {
				JSONObject jsonObject = results.getJSONObject(i);
				resultsCollection.add(new GoogleImageSearchResponseResult().load(jsonObject));
			}
			this.results = resultsCollection;
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();

		if (results != null && !results.isEmpty()) {
			JSONArray resultsCollection = new JSONArray();
			for (GoogleImageSearchResponseResult value : results) {
				resultsCollection.put(value.convertToJson());
			}
			conversion.put("results", resultsCollection);
		}

		return conversion;

	}
}
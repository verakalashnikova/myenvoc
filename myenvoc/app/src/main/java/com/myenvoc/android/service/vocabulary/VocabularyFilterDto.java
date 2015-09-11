package com.myenvoc.android.service.vocabulary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.myenvoc.android.domain.Entity;

/** AUTO-GENERATED **/
public class VocabularyFilterDto implements Entity {

	private String wordStart;
	private String addedAt;
	private String order;
	private List<String> statuses;

	public String getWordStart() {
		return wordStart;
	}

	public void setWordStart(final String wordStart) {
		this.wordStart = wordStart;
	}

	public String getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(final String addedAt) {
		this.addedAt = addedAt;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(final String order) {
		this.order = order;
	}

	public List<String> getStatuses() {
		if (statuses == null) {
			return Collections.emptyList();
		}
		return statuses;
	}

	public void setStatuses(final List<String> statuses) {
		this.statuses = statuses;
	}

	@Override
	public VocabularyFilterDto load(final JSONObject object) throws JSONException {

		this.wordStart = object.optString("wordStart");

		this.addedAt = object.optString("addedAt");

		this.order = object.optString("order");

		JSONArray statuses = object.optJSONArray("statuses");
		if (statuses != null) {
			List<String> statusesCollection = new ArrayList<String>(statuses.length());
			for (int i = 0; i < statuses.length(); i++) {
				String value = statuses.getString(i);
				statusesCollection.add(value);
			}
			this.statuses = statusesCollection;
		}

		return this;
	}

	@Override
	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.wordStart != null) {
			conversion.put("wordStart", this.wordStart);
		}

		if (this.addedAt != null) {
			conversion.put("addedAt", this.addedAt);
		}

		if (this.order != null) {
			conversion.put("order", this.order);
		}

		if (statuses != null && !statuses.isEmpty()) {
			JSONArray statusesCollection = new JSONArray();
			for (Object value : statuses) {
				statusesCollection.put(value);
			}
			conversion.put("statuses", statusesCollection);
		}

		return conversion;

	}
}
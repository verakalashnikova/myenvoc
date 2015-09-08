package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class TranslationMeaning implements Entity {

	private List<TranEntry> entry;
	private String descr;

	public List<TranEntry> getEntry() {
		if (entry == null) {
			return Collections.emptyList();
		}
		return entry;
	}

	public void setEntry(List<TranEntry> entry) {
		this.entry = entry;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public TranslationMeaning load(JSONObject object) throws JSONException {

		this.descr = object.optString("descr");

		JSONArray entry = object.optJSONArray("entry");
		if (entry != null) {
			List<TranEntry> entryCollection = new ArrayList<TranEntry>(entry.length());
			for (int i = 0; i < entry.length(); i++) {
				JSONObject jsonObject = entry.getJSONObject(i);
				entryCollection.add(new TranEntry().load(jsonObject));
			}
			this.entry = entryCollection;
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.descr != null) {
			conversion.put("descr", this.descr);
		}

		if (entry != null && !entry.isEmpty()) {
			JSONArray entryCollection = new JSONArray();
			for (TranEntry value : entry) {
				entryCollection.put(value.convertToJson());
			}
			conversion.put("entry", entryCollection);
		}

		return conversion;

	}
}
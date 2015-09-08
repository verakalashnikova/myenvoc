package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class TranEntry implements Entity {

	private String part;
	private List<TranOfThisPart> data;

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public List<TranOfThisPart> getData() {
		if (data == null) {
			return Collections.emptyList();
		}
		return data;
	}

	public void setData(List<TranOfThisPart> data) {
		this.data = data;
	}

	public TranEntry load(JSONObject object) throws JSONException {

		this.part = object.optString("part");

		JSONArray data = object.optJSONArray("data");
		if (data != null) {
			List<TranOfThisPart> dataCollection = new ArrayList<TranOfThisPart>(data.length());
			for (int i = 0; i < data.length(); i++) {
				JSONObject jsonObject = data.getJSONObject(i);
				dataCollection.add(new TranOfThisPart().load(jsonObject));
			}
			this.data = dataCollection;
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.part != null) {
			conversion.put("part", this.part);
		}

		if (data != null && !data.isEmpty()) {
			JSONArray dataCollection = new JSONArray();
			for (TranOfThisPart value : data) {
				dataCollection.put(value.convertToJson());
			}
			conversion.put("data", dataCollection);
		}

		return conversion;

	}
}
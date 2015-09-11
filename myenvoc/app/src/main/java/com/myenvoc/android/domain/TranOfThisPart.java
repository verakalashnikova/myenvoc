package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class TranOfThisPart implements Entity {

	private List<String> e;
	private List<String> t;

	public List<String> getE() {
		if (e == null) {
			return Collections.emptyList();
		}
		return e;
	}

	public void setE(List<String> e) {
		this.e = e;
	}

	public List<String> getT() {
		if (t == null) {
			return Collections.emptyList();
		}
		return t;
	}

	public void setT(List<String> t) {
		this.t = t;
	}

	public TranOfThisPart load(JSONObject object) throws JSONException {

		JSONArray e = object.optJSONArray("e");
		if (e != null) {
			List<String> eCollection = new ArrayList<String>(e.length());
			for (int i = 0; i < e.length(); i++) {
				String value = e.getString(i);
				eCollection.add(value);
			}
			this.e = eCollection;
		}

		JSONArray t = object.optJSONArray("t");
		if (t != null) {
			List<String> tCollection = new ArrayList<String>(t.length());
			for (int i = 0; i < t.length(); i++) {
				String value = t.getString(i);
				tCollection.add(value);
			}
			this.t = tCollection;
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();

		if (e != null && !e.isEmpty()) {
			JSONArray eCollection = new JSONArray();
			for (Object value : e) {
				eCollection.put(value);
			}
			conversion.put("e", eCollection);
		}

		if (t != null && !t.isEmpty()) {
			JSONArray tCollection = new JSONArray();
			for (Object value : t) {
				tCollection.put(value);
			}
			conversion.put("t", tCollection);
		}

		return conversion;

	}
}
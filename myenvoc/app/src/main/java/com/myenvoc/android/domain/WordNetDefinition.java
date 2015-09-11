package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class WordNetDefinition implements Entity {

	private String l;
	private String t;
	private List<WordNetSynset> s;

	public String getL() {
		return l;
	}

	public void setL(final String l) {
		this.l = l;
	}

	public String getT() {
		return t;
	}

	public void setT(final String t) {
		this.t = t;
	}

	public List<WordNetSynset> getS() {
		if (s == null) {
			return Collections.emptyList();
		}
		return s;
	}

	public void setS(final List<WordNetSynset> s) {
		this.s = s;
	}

	@Override
	public WordNetDefinition load(final JSONObject object) throws JSONException {

		this.l = object.optString("l");

		this.t = object.optString("t");

		JSONArray s = object.optJSONArray("s");
		if (s != null) {
			List<WordNetSynset> sCollection = new ArrayList<WordNetSynset>(s.length());
			for (int i = 0; i < s.length(); i++) {
				JSONObject jsonObject = s.getJSONObject(i);
				sCollection.add(new WordNetSynset().load(jsonObject));
			}
			this.s = sCollection;
		}

		return this;
	}

	@Override
	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.l != null) {
			conversion.put("l", this.l);
		}

		if (this.t != null) {
			conversion.put("t", this.t);
		}

		if (s != null && !s.isEmpty()) {
			JSONArray sCollection = new JSONArray();
			for (WordNetSynset value : s) {
				sCollection.put(value.convertToJson());
			}
			conversion.put("s", sCollection);
		}

		return conversion;

	}
}
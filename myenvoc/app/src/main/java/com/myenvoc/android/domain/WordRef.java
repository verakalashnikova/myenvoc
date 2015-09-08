package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class WordRef implements Entity {

	private String lemma;
	private int id;

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public WordRef load(JSONObject object) throws JSONException {

		this.lemma = object.optString("lemma");

		if (object.opt("id") != null) {
			this.id = (Integer) object.get("id");
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.lemma != null) {
			conversion.put("lemma", this.lemma);
		}

		conversion.put("id", this.id);

		return conversion;

	}
}
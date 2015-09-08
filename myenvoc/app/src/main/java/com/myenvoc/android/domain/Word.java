package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class Word implements Entity {

	private int id;
	private int groupId;
	private String lemma;
	private String transcription;
	private int numberOfEntries;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getTranscription() {
		return transcription;
	}

	public void setTranscription(String transcription) {
		this.transcription = transcription;
	}

	public int getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setNumberOfEntries(int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public Word load(JSONObject object) throws JSONException {

		this.lemma = object.optString("lemma");

		this.transcription = object.optString("transcription");

		if (object.opt("id") != null) {
			this.id = (Integer) object.get("id");
		}

		if (object.opt("groupId") != null) {
			this.groupId = (Integer) object.get("groupId");
		}

		if (object.opt("numberOfEntries") != null) {
			this.numberOfEntries = (Integer) object.get("numberOfEntries");
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		conversion.put("id", this.id);

		conversion.put("groupId", this.groupId);

		if (this.lemma != null) {
			conversion.put("lemma", this.lemma);
		}

		if (this.transcription != null) {
			conversion.put("transcription", this.transcription);
		}

		conversion.put("numberOfEntries", this.numberOfEntries);

		return conversion;

	}
}
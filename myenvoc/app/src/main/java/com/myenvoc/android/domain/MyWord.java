package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** THIS CLASS WAS CHANGED FROM AUTO-GENERATION VERSION. **/
public class MyWord implements Entity {

	private MyWordAttributes attributes;

	public MyWordAttributes getAttributes() {
		if (attributes == null) {
			attributes = new MyWordAttributes();
		}
		return attributes;
	}

	public void invalidateAttributes() {
		attributes = null;
	}

	private int wordId;
	private int groupId;
	private long textId;
	private long id;
	private String lemma;
	private String imageUrl;
	private String translation;
	private String transcription;
	private MyWordStatus status;
	private Date added;
	private List<String> tags;
	private List<MyWordContext> contexts;

	public int getWordId() {
		return wordId;
	}

	public void setWordId(final int wordId) {
		this.wordId = wordId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(final int groupId) {
		this.groupId = groupId;
	}

	public long getTextId() {
		return textId;
	}

	public void setTextId(final long textId) {
		this.textId = textId;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(final String lemma) {
		this.lemma = lemma;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(final String translation) {
		this.translation = translation;
	}

	public String getTranscription() {
		return transcription;
	}

	public void setTranscription(final String transcription) {
		this.transcription = transcription;
	}

	public MyWordStatus getStatus() {
		return status;
	}

	public void setStatus(final MyWordStatus status) {
		this.status = status;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(final Date added) {
		this.added = added;
	}

	public List<String> getTags() {
		if (tags == null) {
			return Collections.emptyList();
		}
		return tags;
	}

	public void setTags(final List<String> tags) {
		this.tags = tags;
	}

	public List<MyWordContext> getContexts() {
		if (contexts == null) {
			return Collections.emptyList();
		}
		return contexts;
	}

	public void setContexts(final List<MyWordContext> contexts) {
		this.contexts = contexts;
	}

	@Override
	public MyWord load(final JSONObject object) throws JSONException {

		this.lemma = object.optString("lemma");

		this.imageUrl = object.optString("imageUrl");

		this.translation = object.optString("translation");

		this.transcription = object.optString("transcription");

		if (object.opt("wordId") != null) {
			this.wordId = (Integer) object.get("wordId");
		}

		if (object.opt("groupId") != null) {
			this.groupId = (Integer) object.get("groupId");
		}

		long added = (long) object.optDouble("added", -1);
		if (added > 0) {
			this.added = new Date(added);
		}

		String status = object.optString("status");
		if (!"".equals(status)) {
			this.status = MyWordStatus.valueOf(status);
		}

		JSONArray contexts = object.optJSONArray("contexts");
		if (contexts != null) {
			List<MyWordContext> contextsCollection = new ArrayList<MyWordContext>(contexts.length());
			for (int i = 0; i < contexts.length(); i++) {
				JSONObject jsonObject = contexts.getJSONObject(i);
				contextsCollection.add(new MyWordContext().load(jsonObject));
			}
			this.contexts = contextsCollection;
		}

		JSONArray tags = object.optJSONArray("tags");
		if (tags != null) {
			List<String> tagsCollection = new ArrayList<String>(tags.length());
			for (int i = 0; i < tags.length(); i++) {
				String value = tags.getString(i);
				tagsCollection.add(value);
			}
			this.tags = tagsCollection;
		}

		return this;
	}

	@Override
	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		conversion.put("wordId", this.wordId);

		conversion.put("groupId", this.groupId);

		if (this.lemma != null) {
			conversion.put("lemma", this.lemma);
		}

		if (this.imageUrl != null) {
			conversion.put("imageUrl", this.imageUrl);
		}

		if (this.translation != null) {
			conversion.put("translation", this.translation);
		}

		if (this.transcription != null) {
			conversion.put("transcription", this.transcription);
		}

		if (this.added != null) {
			conversion.put("added", this.added.getTime());
		}

		if (this.status != null) {
			conversion.put("status", this.status.name());
		}

		if (tags != null && !tags.isEmpty()) {
			JSONArray tagsCollection = new JSONArray();
			for (Object value : tags) {
				tagsCollection.put(value);
			}
			conversion.put("tags", tagsCollection);
		}

		if (contexts != null && !contexts.isEmpty()) {
			JSONArray contextsCollection = new JSONArray();
			for (MyWordContext value : contexts) {
				contextsCollection.put(value.convertToJson());
			}
			conversion.put("contexts", contextsCollection);
		}

		return conversion;

	}

}
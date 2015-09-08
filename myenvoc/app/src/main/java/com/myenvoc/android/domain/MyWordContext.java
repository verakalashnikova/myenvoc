package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class MyWordContext implements Entity {

	private SynsetType synsetType;
	private String synonyms;
	private String definition;
	private String example;

	public SynsetType getSynsetType() {
		return synsetType;
	}

	public void setSynsetType(final SynsetType synsetType) {
		this.synsetType = synsetType;
	}

	public String getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(final String synonyms) {
		this.synonyms = synonyms;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(final String definition) {
		this.definition = definition;
	}

	public String getExample() {
		return example;
	}

	public void setExample(final String example) {
		this.example = example;
	}

	@Override
	public MyWordContext load(final JSONObject object) throws JSONException {

		this.synonyms = object.optString("synonyms");

		this.definition = object.optString("definition");

		this.example = object.optString("example");

		String synsetType = object.optString("synsetType");
		if (!"".equals(synsetType)) {
			this.synsetType = SynsetType.valueOf(synsetType);
		}

		return this;
	}

	@Override
	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();

		if (this.synonyms != null) {
			conversion.put("synonyms", this.synonyms);
		}

		if (this.definition != null) {
			conversion.put("definition", this.definition);
		}

		if (this.example != null) {
			conversion.put("example", this.example);
		}

		if (this.synsetType != null) {
			conversion.put("synsetType", this.synsetType.name());
		}

		return conversion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((definition == null) ? 0 : definition.hashCode());
		result = prime * result + ((example == null) ? 0 : example.hashCode());
		result = prime * result + ((synonyms == null) ? 0 : synonyms.hashCode());
		result = prime * result + ((synsetType == null) ? 0 : synsetType.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyWordContext other = (MyWordContext) obj;
		if (definition == null) {
			if (other.definition != null)
				return false;
		} else if (!definition.equals(other.definition))
			return false;
		if (example == null) {
			if (other.example != null)
				return false;
		} else if (!example.equals(other.example))
			return false;
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
		if (synsetType != other.synsetType)
			return false;
		return true;
	}

}
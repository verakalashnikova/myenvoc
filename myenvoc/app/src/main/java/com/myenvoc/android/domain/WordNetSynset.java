package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/** AUTO-GENERATED **/
public class WordNetSynset implements Entity {

	private SynsetType synsetType;
	private List<WordRef> synonyms;
	private String definition;
	private String example;

	public SynsetType getSynsetType() {
		return synsetType;
	}

	public void setSynsetType(SynsetType synsetType) {
		this.synsetType = synsetType;
	}

	public List<WordRef> getSynonyms() {
			if (synonyms == null) {
				return Collections.emptyList();
			}
		return synonyms;
	}

	public void setSynonyms(List<WordRef> synonyms) {
		this.synonyms = synonyms;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public WordNetSynset load(JSONObject object) throws JSONException {

		this.definition = object.optString("definition");

		this.example = object.optString("example");

		String synsetType = object.optString("synsetType");
		if (!"".equals(synsetType)) {
			this.synsetType = SynsetType.valueOf(synsetType);
		}





			JSONArray synonyms = object.optJSONArray("synonyms");
			if (synonyms != null) {
				List<WordRef> synonymsCollection = new ArrayList<WordRef>(synonyms.length());
				for (int i = 0; i < synonyms.length(); i++) {
					JSONObject jsonObject = synonyms.getJSONObject(i);
					synonymsCollection.add(new WordRef().load(jsonObject));
				}
				this.synonyms = synonymsCollection;
			}


		
		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.definition != null) {
			conversion.put("definition", this.definition);
		}


		if (this.example != null) {
			conversion.put("example", this.example);
		}


		if (this.synsetType != null) {	
			conversion.put("synsetType", this.synsetType.name());
		}





			if (synonyms != null && !synonyms.isEmpty()) {
				JSONArray synonymsCollection = new JSONArray();
				for (WordRef value : synonyms) {
					synonymsCollection.put(value.convertToJson());
				}
				conversion.put("synonyms", synonymsCollection);
			}




		return conversion;

	}
}
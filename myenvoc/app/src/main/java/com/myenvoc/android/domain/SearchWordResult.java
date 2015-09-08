package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class SearchWordResult implements Entity {

	private List<Status> status;
	private List<TranslationMeaning> translation;
	private List<WordNetSynset> definition;
	private Word word;
	private MyWord myWord;

	public List<Status> getStatus() {
		if (status == null) {
			return Collections.emptyList();
		}
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}

	public List<TranslationMeaning> getTranslation() {
		if (translation == null) {
			return Collections.emptyList();
		}
		return translation;
	}

	public void setTranslation(List<TranslationMeaning> translation) {
		this.translation = translation;
	}

	public List<WordNetSynset> getDefinition() {
		if (definition == null) {
			return Collections.emptyList();
		}
		return definition;
	}

	public void setDefinition(List<WordNetSynset> definition) {
		this.definition = definition;
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public MyWord getMyWord() {
		return myWord;
	}

	public void setMyWord(MyWord myWord) {
		this.myWord = myWord;
	}

	public SearchWordResult load(JSONObject object) throws JSONException {

		JSONObject word = object.optJSONObject("word");
		if (word != null) {
			this.word = new Word().load(word);
		}

		JSONObject myWord = object.optJSONObject("myWord");
		if (myWord != null) {
			this.myWord = new MyWord().load(myWord);
		}

		JSONArray status = object.optJSONArray("status");
		if (status != null) {
			List<Status> statusCollection = new ArrayList<Status>(status.length());
			for (int i = 0; i < status.length(); i++) {
				JSONObject jsonObject = status.getJSONObject(i);
				statusCollection.add(new Status().load(jsonObject));
			}
			this.status = statusCollection;
		}

		JSONArray translation = object.optJSONArray("translation");
		if (translation != null) {
			List<TranslationMeaning> translationCollection = new ArrayList<TranslationMeaning>(translation.length());
			for (int i = 0; i < translation.length(); i++) {
				JSONObject jsonObject = translation.getJSONObject(i);
				translationCollection.add(new TranslationMeaning().load(jsonObject));
			}
			this.translation = translationCollection;
		}

		JSONArray definition = object.optJSONArray("definition");
		if (definition != null) {
			List<WordNetSynset> definitionCollection = new ArrayList<WordNetSynset>(definition.length());
			for (int i = 0; i < definition.length(); i++) {
				JSONObject jsonObject = definition.getJSONObject(i);
				definitionCollection.add(new WordNetSynset().load(jsonObject));
			}
			this.definition = definitionCollection;
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.word != null) {
			conversion.put("word", this.word.convertToJson());
		}

		if (this.myWord != null) {
			conversion.put("myWord", this.myWord.convertToJson());
		}

		if (status != null && !status.isEmpty()) {
			JSONArray statusCollection = new JSONArray();
			for (Status value : status) {
				statusCollection.put(value.convertToJson());
			}
			conversion.put("status", statusCollection);
		}

		if (translation != null && !translation.isEmpty()) {
			JSONArray translationCollection = new JSONArray();
			for (TranslationMeaning value : translation) {
				translationCollection.put(value.convertToJson());
			}
			conversion.put("translation", translationCollection);
		}

		if (definition != null && !definition.isEmpty()) {
			JSONArray definitionCollection = new JSONArray();
			for (WordNetSynset value : definition) {
				definitionCollection.put(value.convertToJson());
			}
			conversion.put("definition", definitionCollection);
		}

		return conversion;

	}
}
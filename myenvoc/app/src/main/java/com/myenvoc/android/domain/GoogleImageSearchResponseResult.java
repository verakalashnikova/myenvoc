package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class GoogleImageSearchResponseResult implements Entity {

	private String url;
	private String tbUrl;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTbUrl() {
		return tbUrl;
	}

	public void setTbUrl(String tbUrl) {
		this.tbUrl = tbUrl;
	}

	public GoogleImageSearchResponseResult load(JSONObject object) throws JSONException {

		this.url = object.optString("url");

		this.tbUrl = object.optString("tbUrl");

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.url != null) {
			conversion.put("url", this.url);
		}

		if (this.tbUrl != null) {
			conversion.put("tbUrl", this.tbUrl);
		}

		return conversion;

	}
}
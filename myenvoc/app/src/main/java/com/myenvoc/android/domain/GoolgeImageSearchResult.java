package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class GoolgeImageSearchResult implements Entity {

	private GoogleImageSearchResponseData responseData;
	private String responseDetails;
	private String responseStatus;

	public GoogleImageSearchResponseData getResponseData() {
		return responseData;
	}

	public void setResponseData(GoogleImageSearchResponseData responseData) {
		this.responseData = responseData;
	}

	public String getResponseDetails() {
		return responseDetails;
	}

	public void setResponseDetails(String responseDetails) {
		this.responseDetails = responseDetails;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public GoolgeImageSearchResult load(JSONObject object) throws JSONException {

		JSONObject responseData = object.optJSONObject("responseData");
		if (responseData != null) {
			this.responseData = new GoogleImageSearchResponseData().load(responseData);
		}

		this.responseDetails = object.optString("responseDetails");

		this.responseStatus = object.optString("responseStatus");

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.responseData != null) {
			conversion.put("responseData", this.responseData.convertToJson());
		}

		if (this.responseDetails != null) {
			conversion.put("responseDetails", this.responseDetails);
		}

		if (this.responseStatus != null) {
			conversion.put("responseStatus", this.responseStatus);
		}

		return conversion;

	}
}
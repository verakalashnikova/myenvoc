package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class Status implements Entity {

	private String code;
	private String param;
	private String value;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Status load(JSONObject object) throws JSONException {

		this.code = object.optString("code");

		this.param = object.optString("param");

		this.value = object.optString("value");

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.code != null) {
			conversion.put("code", this.code);
		}

		if (this.param != null) {
			conversion.put("param", this.param);
		}

		if (this.value != null) {
			conversion.put("value", this.value);
		}

		return conversion;

	}
}
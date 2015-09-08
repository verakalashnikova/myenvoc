package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/** AUTO-GENERATED **/
public class Failure implements Entity {

	private FailureValue value;

	private String localizedValue;

	public static Failure byValue(final FailureValue value) {
		Failure failure = new Failure();
		failure.setValue(value);
		return failure;
	}

	public static Failure byLocalizedValue(final String localizedValue) {
		Failure failure = new Failure();
		failure.localizedValue = localizedValue == null ? "" : localizedValue;
		return failure;
	}

	public String getLocalized(final Context context) {
		return value != null ? value.getLocalized(context) : localizedValue;
	}

	public FailureValue getValue() {
		return value;
	}

	public void setValue(final FailureValue value) {
		this.value = value;
	}

	@Override
	public Failure load(final JSONObject object) throws JSONException {

		String value = object.optString("value");
		if (!"".equals(value)) {
			this.value = FailureValue.valueOf(value);
		}

		return this;
	}

	@Override
	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.value != null) {
			conversion.put("value", this.value.name());
		}

		return conversion;

	}

	@Override
	public String toString() {
		return value.toString();
	}
}
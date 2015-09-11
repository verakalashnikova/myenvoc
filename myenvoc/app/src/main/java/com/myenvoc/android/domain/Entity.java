package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

public interface Entity {
	Object load(JSONObject object) throws JSONException;

	JSONObject convertToJson() throws JSONException;
}

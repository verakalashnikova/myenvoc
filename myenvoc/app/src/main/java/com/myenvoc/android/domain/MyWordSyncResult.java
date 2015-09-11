package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class MyWordSyncResult implements Entity {

	private int serverId;
	private int serverVersion;
	private MyWord myWord;
	private int clientId;

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(int serverVersion) {
		this.serverVersion = serverVersion;
	}

	public MyWord getMyWord() {
		return myWord;
	}

	public void setMyWord(MyWord myWord) {
		this.myWord = myWord;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public MyWordSyncResult load(JSONObject object) throws JSONException {

		JSONObject myWord = object.optJSONObject("myWord");
		if (myWord != null) {
			this.myWord = new MyWord().load(myWord);
		}

		if (object.opt("serverId") != null) {
			this.serverId = (Integer) object.get("serverId");
		}

		if (object.opt("serverVersion") != null) {
			this.serverVersion = (Integer) object.get("serverVersion");
		}

		if (object.opt("clientId") != null) {
			this.clientId = (Integer) object.get("clientId");
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		if (this.myWord != null) {
			conversion.put("myWord", this.myWord.convertToJson());
		}

		conversion.put("serverId", this.serverId);

		conversion.put("serverVersion", this.serverVersion);

		conversion.put("clientId", this.clientId);

		return conversion;

	}
}
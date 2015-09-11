package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class MyWordSync implements Entity {

	private int serverId;
	private int serverVersion;
	private int clientId;
	private boolean newOrUpdated;
	private boolean removed;
	private MyWord myWord;

	public MyWordSync(int serverId, int serverVersion, int clientId, boolean newOrUpdated, boolean removed,
			MyWord myWord) {
		super();
		this.serverId = serverId;
		this.serverVersion = serverVersion;
		this.clientId = clientId;
		this.newOrUpdated = newOrUpdated;
		this.removed = removed;
		this.myWord = myWord;
	}

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

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public boolean isNewOrUpdated() {
		return newOrUpdated;
	}

	public void setNewOrUpdated(boolean newOrUpdated) {
		this.newOrUpdated = newOrUpdated;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public MyWord getMyWord() {
		return myWord;
	}

	public void setMyWord(MyWord myWord) {
		this.myWord = myWord;
	}

	public MyWordSync load(JSONObject object) throws JSONException {

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

		if (object.opt("newOrUpdated") != null) {
			this.newOrUpdated = (Boolean) object.get("newOrUpdated");
		}

		if (object.opt("removed") != null) {
			this.removed = (Boolean) object.get("removed");
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

		conversion.put("newOrUpdated", this.newOrUpdated);

		conversion.put("removed", this.removed);

		return conversion;

	}
}
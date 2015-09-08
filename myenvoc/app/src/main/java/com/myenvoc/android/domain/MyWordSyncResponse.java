package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** AUTO-GENERATED **/
public class MyWordSyncResponse implements Entity {

	private boolean success;
	private List<MyWordSyncResult> addedOnServer;
	private List<MyWordSyncResult> updatedOnServer;
	private List<MyWordSyncResult> removedOnServer;
	private List<MyWordSyncResult> localAddUpdateConfirmedByServer;
	private List<MyWordSyncResult> localRemoveConfirmedByServer;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<MyWordSyncResult> getAddedOnServer() {
		if (addedOnServer == null) {
			return Collections.emptyList();
		}
		return addedOnServer;
	}

	public void setAddedOnServer(List<MyWordSyncResult> addedOnServer) {
		this.addedOnServer = addedOnServer;
	}

	public List<MyWordSyncResult> getUpdatedOnServer() {
		if (updatedOnServer == null) {
			return Collections.emptyList();
		}
		return updatedOnServer;
	}

	public void setUpdatedOnServer(List<MyWordSyncResult> updatedOnServer) {
		this.updatedOnServer = updatedOnServer;
	}

	public List<MyWordSyncResult> getRemovedOnServer() {
		if (removedOnServer == null) {
			return Collections.emptyList();
		}
		return removedOnServer;
	}

	public void setRemovedOnServer(List<MyWordSyncResult> removedOnServer) {
		this.removedOnServer = removedOnServer;
	}

	public List<MyWordSyncResult> getLocalAddUpdateConfirmedByServer() {
		if (localAddUpdateConfirmedByServer == null) {
			return Collections.emptyList();
		}
		return localAddUpdateConfirmedByServer;
	}

	public void setLocalAddUpdateConfirmedByServer(List<MyWordSyncResult> localAddUpdateConfirmedByServer) {
		this.localAddUpdateConfirmedByServer = localAddUpdateConfirmedByServer;
	}

	public List<MyWordSyncResult> getLocalRemoveConfirmedByServer() {
		if (localRemoveConfirmedByServer == null) {
			return Collections.emptyList();
		}
		return localRemoveConfirmedByServer;
	}

	public void setLocalRemoveConfirmedByServer(List<MyWordSyncResult> localRemoveConfirmedByServer) {
		this.localRemoveConfirmedByServer = localRemoveConfirmedByServer;
	}

	public MyWordSyncResponse load(JSONObject object) throws JSONException {

		if (object.opt("success") != null) {
			this.success = (Boolean) object.get("success");
		}

		JSONArray addedOnServer = object.optJSONArray("addedOnServer");
		if (addedOnServer != null) {
			List<MyWordSyncResult> addedOnServerCollection = new ArrayList<MyWordSyncResult>(addedOnServer.length());
			for (int i = 0; i < addedOnServer.length(); i++) {
				JSONObject jsonObject = addedOnServer.getJSONObject(i);
				addedOnServerCollection.add(new MyWordSyncResult().load(jsonObject));
			}
			this.addedOnServer = addedOnServerCollection;
		}

		JSONArray updatedOnServer = object.optJSONArray("updatedOnServer");
		if (updatedOnServer != null) {
			List<MyWordSyncResult> updatedOnServerCollection = new ArrayList<MyWordSyncResult>(updatedOnServer.length());
			for (int i = 0; i < updatedOnServer.length(); i++) {
				JSONObject jsonObject = updatedOnServer.getJSONObject(i);
				updatedOnServerCollection.add(new MyWordSyncResult().load(jsonObject));
			}
			this.updatedOnServer = updatedOnServerCollection;
		}

		JSONArray removedOnServer = object.optJSONArray("removedOnServer");
		if (removedOnServer != null) {
			List<MyWordSyncResult> removedOnServerCollection = new ArrayList<MyWordSyncResult>(removedOnServer.length());
			for (int i = 0; i < removedOnServer.length(); i++) {
				JSONObject jsonObject = removedOnServer.getJSONObject(i);
				removedOnServerCollection.add(new MyWordSyncResult().load(jsonObject));
			}
			this.removedOnServer = removedOnServerCollection;
		}

		JSONArray localAddUpdateConfirmedByServer = object.optJSONArray("localAddUpdateConfirmedByServer");
		if (localAddUpdateConfirmedByServer != null) {
			List<MyWordSyncResult> localAddUpdateConfirmedByServerCollection = new ArrayList<MyWordSyncResult>(
					localAddUpdateConfirmedByServer.length());
			for (int i = 0; i < localAddUpdateConfirmedByServer.length(); i++) {
				JSONObject jsonObject = localAddUpdateConfirmedByServer.getJSONObject(i);
				localAddUpdateConfirmedByServerCollection.add(new MyWordSyncResult().load(jsonObject));
			}
			this.localAddUpdateConfirmedByServer = localAddUpdateConfirmedByServerCollection;
		}

		JSONArray localRemoveConfirmedByServer = object.optJSONArray("localRemoveConfirmedByServer");
		if (localRemoveConfirmedByServer != null) {
			List<MyWordSyncResult> localRemoveConfirmedByServerCollection = new ArrayList<MyWordSyncResult>(
					localRemoveConfirmedByServer.length());
			for (int i = 0; i < localRemoveConfirmedByServer.length(); i++) {
				JSONObject jsonObject = localRemoveConfirmedByServer.getJSONObject(i);
				localRemoveConfirmedByServerCollection.add(new MyWordSyncResult().load(jsonObject));
			}
			this.localRemoveConfirmedByServer = localRemoveConfirmedByServerCollection;
		}

		return this;
	}

	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		conversion.put("success", this.success);

		if (addedOnServer != null && !addedOnServer.isEmpty()) {
			JSONArray addedOnServerCollection = new JSONArray();
			for (MyWordSyncResult value : addedOnServer) {
				addedOnServerCollection.put(value.convertToJson());
			}
			conversion.put("addedOnServer", addedOnServerCollection);
		}

		if (updatedOnServer != null && !updatedOnServer.isEmpty()) {
			JSONArray updatedOnServerCollection = new JSONArray();
			for (MyWordSyncResult value : updatedOnServer) {
				updatedOnServerCollection.put(value.convertToJson());
			}
			conversion.put("updatedOnServer", updatedOnServerCollection);
		}

		if (removedOnServer != null && !removedOnServer.isEmpty()) {
			JSONArray removedOnServerCollection = new JSONArray();
			for (MyWordSyncResult value : removedOnServer) {
				removedOnServerCollection.put(value.convertToJson());
			}
			conversion.put("removedOnServer", removedOnServerCollection);
		}

		if (localAddUpdateConfirmedByServer != null && !localAddUpdateConfirmedByServer.isEmpty()) {
			JSONArray localAddUpdateConfirmedByServerCollection = new JSONArray();
			for (MyWordSyncResult value : localAddUpdateConfirmedByServer) {
				localAddUpdateConfirmedByServerCollection.put(value.convertToJson());
			}
			conversion.put("localAddUpdateConfirmedByServer", localAddUpdateConfirmedByServerCollection);
		}

		if (localRemoveConfirmedByServer != null && !localRemoveConfirmedByServer.isEmpty()) {
			JSONArray localRemoveConfirmedByServerCollection = new JSONArray();
			for (MyWordSyncResult value : localRemoveConfirmedByServer) {
				localRemoveConfirmedByServerCollection.put(value.convertToJson());
			}
			conversion.put("localRemoveConfirmedByServer", localRemoveConfirmedByServerCollection);
		}

		return conversion;

	}
}
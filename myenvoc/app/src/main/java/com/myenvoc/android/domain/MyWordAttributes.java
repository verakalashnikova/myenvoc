package com.myenvoc.android.domain;

public class MyWordAttributes {

	private int id = -1;
	private int serverId;
	private int serverVersion;

	public boolean isNew() {

		return id < 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

}

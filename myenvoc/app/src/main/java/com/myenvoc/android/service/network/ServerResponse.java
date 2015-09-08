package com.myenvoc.android.service.network;

import java.util.Collection;

import com.myenvoc.android.domain.Entity;

/***
 * Also status will go here (Internet connection failure, no results, etc.)
 */
public class ServerResponse<T> {
	private T result;

	private boolean success;

	private Object payload;

	public static <T> ServerResponse<T> systemFailure(Object payload) {
		return new ServerResponse<T>(null, payload, false);
	}

	public ServerResponse(T result, Object payload, boolean success) {
		super();
		this.result = result;
		this.success = success;
		this.payload = payload;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getResult() {
		return result;
	}
	
	public Object getPayload() {
		return payload;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public static ServerResponse<Collection<? extends Entity>> success(Collection<? extends Entity> entities, Object payload) {
		return new ServerResponse<Collection<? extends Entity>>(entities, payload, true);
	}

	public static ServerResponse<? extends Entity> success(Entity entity, Object payload) {
		return new ServerResponse<Entity>(entity, payload, true);
	}

}

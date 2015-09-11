package com.myenvoc.android.service.network;

import com.myenvoc.android.service.network.Resource.ServerRequestBuilder;
import com.myenvoc.commons.MyenvocException;

/***
 * Abstract thing which knows how to get data from server.
 */
public class ServerRequest {
	public enum RequestType {
		POST, GET
	}

	private final String url;
	private final RequestType requestType;
	private final Class<?> classToBuild;
	private final boolean collection;
	private final String postParameter;
	private final String authToken;

	ServerRequest(final ServerRequestBuilder builder) {
		this.url = builder.urlBuilder.toString();
		this.requestType = builder.requestType;
		this.classToBuild = builder.classToBuild;
		this.collection = builder.collection;
		this.postParameter = builder.postParameter;
		if (builder.authenticationRequired && builder.authToken == null) {
			throw new MyenvocException("This request need auth token to be set");
		}
		this.authToken = builder.authToken;
	}

	public String getPostParameter() {
		return postParameter;
	}

	public boolean isCollection() {
		return collection;
	}

	public Class<?> getClassToBuild() {
		return classToBuild;
	}

	public String getUrl() {
		return url;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public String getAuthToken() {
		return authToken;
	}

	@Override
	public String toString() {
		return url;
	}
}

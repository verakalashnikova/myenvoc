package com.myenvoc.android.service.network;

import java.util.Collection;

import com.myenvoc.android.service.network.ServerRequest.RequestType;
import com.myenvoc.commons.CommonUtils;

public class Resource {
	private final String url;
	private final boolean authenticationRequired;

	protected Resource(final String url, final boolean authenticationRequired) {
		assert url != null;
		this.url = url;
		this.authenticationRequired = authenticationRequired;
	}

	public static Resource forUrl(final String url) {
		return new Resource(CommonUtils.removeTrailingSlash(url), false);
	}

	public Resource path(final String path) {
		return path(path, false);
	}

	public Resource path(final String path, final boolean authenticationRequired) {
		return new Resource(url + "/" + path, authenticationRequired);
	}

	public ServerRequestBuilder requestBuilderInitParams() {
		return new ServerRequestBuilder(this, true, RequestType.GET);
	}

	public ServerRequestBuilder requestBuilder() {
		return new ServerRequestBuilder(this, false, RequestType.GET);
	}

	public ServerRequestBuilder requestBuilder(final RequestType requestType) {
		return new ServerRequestBuilder(this, false, requestType);
	}

	public static class ServerRequestBuilder {
		final StringBuilder urlBuilder;
		RequestType requestType;
		Class<?> classToBuild;
		boolean collection;
		String postParameter;
		boolean authenticationRequired;
		String authToken;

		private ServerRequestBuilder(final Resource resource, final boolean initParams, final RequestType requestType) {
			urlBuilder = new StringBuilder(resource.url);
			this.authenticationRequired = resource.authenticationRequired;
			if (requestType == RequestType.GET) {
				if (initParams) {
					urlBuilder.append('?');
				} else {
					urlBuilder.append('&');
				}
			}
			this.requestType = requestType;

		}

		public ServerRequest buildFor(final Class<?> classToBuild) {
			return buildFor(classToBuild, false);
		}

		public ServerRequest buildFor(final Class<?> classToBuild, final boolean collection) {
			this.classToBuild = classToBuild;
			this.collection = collection;
			return new ServerRequest(this);
		}

		public ServerRequestBuilder withPOSTParameter(final String postParameter) {
			this.postParameter = postParameter;
			return this;
		}

		public ServerRequestBuilder withAuthToken(final String authToken) {
			this.authToken = authToken;
			return this;
		}

		public ServerRequestBuilder queryParameters(final String name, final Collection<? extends Object> values) {
			if (values != null) {
				for (Object value : values) {
					queryParameter(name, value);
				}
			}
			return this;
		}

		public ServerRequestBuilder queryParameter(final String name, final Object value) {
			assert name != null;

			if (value != null) {
				urlBuilder.append(name);
				urlBuilder.append('=');
				urlBuilder.append(CommonUtils.encodeQueryString(value.toString()));
				urlBuilder.append('&');
			}

			return this;
		}

		// FIXME: just sketch, implement
		public void postBody(final Object requestBody) {
			// this.body - requestBodyl
			requestType = RequestType.POST;
		}

	}

}

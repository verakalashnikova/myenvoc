package com.myenvoc.android.service.network;

import android.content.Context;
import android.net.ConnectivityManager;

import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.FailureValue;
import com.myenvoc.android.inject.ContextAware;
import com.myenvoc.android.service.network.ServerRequest.RequestType;
import com.myenvoc.commons.ExecutorFactory;
import com.myenvoc.commons.MyenvocRemoteInvocationException;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.inject.Inject;

/*import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;*/



/** Low level generic networking. **/
public class NetworkService {
	private static final int TIMEOUT = 17000;

	private static final String TAG = NetworkService.class.getName();

	private Context context;

	/** public only for tests */
	@Inject
	public static NetworkService instance;

	@Inject
	private ExecutorFactory executorFactory;

	public static NetworkService getNetworkService() {
		return instance;
	}

	/** only for tests. */
	public NetworkService() {

	}

	@Inject
	public NetworkService(final ContextAware contextAware) {
		this.context = contextAware.getContextFromInstance();
	}

	/*private static DefaultHttpClient client;

	private synchronized static DefaultHttpClient getThreadSafeClient() {

		if (client != null)
			return client;

		client = new DefaultHttpClient();

		ClientConnectionManager mgr = client.getConnectionManager();

		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT);
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

		return client;
	}*/

	/**
	 * @throws MyenvocRemoteInvocationException
	 *             in case of invocation error
	 */
	/*public <T> T synchronousRequest(final ServerRequest request) {
		checkConnection(null);
		switch (request.getRequestType()) {
		case GET:
			return get(request);
		case POST:
			return post(request);
		}
		throw new RuntimeException("Unknown request: " + request.getRequestType());
	}*/

	protected <T> boolean checkConnection(final GenericCallback<T> callback) {

		boolean connected = isConnected();
		if (!connected) {
			Failure failure = Failure.byValue(FailureValue.NO_INTERNET);
			if (callback == null) {
				MyenvocRemoteInvocationException exception = new MyenvocRemoteInvocationException("Unable to establish connection");
				exception.setFailure(failure);
				throw exception;
			} else {
				callback.onError(failure);
			}
		}
		return connected;
	}

	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean connected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected();
		return connected;
	}

	public <T> void asynchronousPOSTRequest(final ServerRequest request, final GenericCallback<T> callback) {
		if (!checkConnection(callback)) {
			return;
		}
		if (request.getRequestType() != RequestType.POST) {
			throw new RuntimeException("Unexpected request: " + request.getRequestType());
		}

		/*executorFactory.execute(ThreadPoolExecutor.TP_EXECUTOR_DEFAULT_PARAMETERS, new MyAsyncTask<ServerRequest, Void>() {

			@Override
			protected Void doInBackgroundThread(final ServerRequest param) {
				try {
					T result = post(param);
					callback.onSuccess(result);
				} catch (MyenvocRemoteInvocationException e) {
					Log.e(TAG, "Error while POST requesting", e);
					callback.onError(e.getFailure());
				} catch (Exception e) {
					Log.e(TAG, "Error while POST requesting", e);
					callback.onError(null);
				}

				return null;
			}

		}, request);*/
	}

	/*private <T> T post(final ServerRequest request) {
		Log.i(TAG, "Invoke HTTP POST for url: " + request.getUrl());

		HttpPost httpPost = new HttpPost(request.getUrl());
		try {
			if (request.getPostParameter() != null) {
				ByteArrayEntity input = new ByteArrayEntity(request.getPostParameter().getBytes("UTF-8"));

				input.setContentType("application/json");
				httpPost.setEntity(input);
			}
		} catch (Exception e) {
			throw new MyenvocException(e);
		}

		return invokeHttpRequest(request, httpPost);
	}*/

	/*private <T> T get(final ServerRequest request) {
		Log.i(TAG, "Invoke HTTP GET for url: " + request.getUrl());
		return invokeHttpRequest(request, new HttpGet(request.getUrl()));
	}

	@SuppressWarnings("unchecked")
	private <T> T invokeHttpRequest(final ServerRequest request, final HttpRequestBase httprequest) {
		HttpClient client = getThreadSafeClient();
		InputStream content = null;
		try {
			if (request.getAuthToken() != null) {
				httprequest.setHeader("X-AjaxRequest", request.getAuthToken());
			}

			StringBuilder builder = new StringBuilder();

			HttpResponse response = client.execute(httprequest);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200 || statusCode == 500) {
				HttpEntity entity = response.getEntity();
				content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				throw new MyenvocRemoteInvocationException("Received " + statusCode + " status code");
			}
			String json = builder.toString();

			if (statusCode == 200) {

				if (request.isCollection()) {
					if (StringUtils.isEmpty(json)) {
						return (T) Collections.emptyList();
					}
					return (T) JSONConverter.loadCollection(json, (Class<Entity>) request.getClassToBuild());
				}
				if (StringUtils.isEmpty(json)) {
					return null;
				}

				return (T) JSONConverter.loadSingleObject(json, (Class<Entity>) request.getClassToBuild());
			} else if (statusCode == 500) {
				throw new MyenvocRemoteInvocationException("Explicit server error 500", JSONConverter.loadSingleObject(json,
						com.myenvoc.android.domain.Failure.class));
			} else {
				throw new MyenvocRemoteInvocationException("Received " + statusCode + " status code. Can't unmarshall error");
			}

		} catch (MyenvocRemoteInvocationException e) {
			Log.e(TAG, "Error while invoking " + request, e);
			throw e;
		} catch (ConnectTimeoutException e) {
			Log.e(TAG, "Error while invoking " + request, e);
			throw new MyenvocRemoteInvocationException("Remote invocation error", Failure.byValue(FailureValue.CONNECTION_TIMEOUT), e);
		} catch (SocketTimeoutException e) {
			Log.e(TAG, "Error while invoking " + request, e);
			throw new MyenvocRemoteInvocationException("Remote invocation error", Failure.byValue(FailureValue.CONNECTION_TIMEOUT), e);
		} catch (Exception e) {
			Log.e(TAG, "Error while invoking " + request, e);
			throw new MyenvocRemoteInvocationException("Remote invocation error", e);
		} finally {
			if (content != null) {
				try {
					content.close();
				} catch (IOException e) {
				}
			}
		}
	}*/

}

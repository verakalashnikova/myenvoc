package com.myenvoc.android.service.dictionary;

import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.FailureValue;
import com.myenvoc.android.optimization.GenericObjectCache;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.commons.ExecutorFactory;
import com.myenvoc.commons.MyAsyncTask;
import com.myenvoc.commons.MyTaskExecutor.ExecutorParameters;
import com.myenvoc.commons.MyenvocRemoteInvocationException;

public abstract class AbstractLoader<RESULT> {
	private static final String TAG = AbstractLoader.class.getName();

	public void load(final String request, final GenericCallback<RESULT> callback, final ExecutorParameters executorParameters) {

		/** On UI thread, as it's fast. **/
		@SuppressWarnings("unchecked")
		RESULT value = (RESULT) GenericObjectCache.get(getCacheKey(request));
		if (value != null) {
			callback.onSuccess(value);
			return;
		}
		ExecutorFactory.getInstance().execute(executorParameters, new MyAsyncTask<String, RESULT>() {
			private volatile Exception exception;

			@Override
			protected RESULT doInBackgroundThread(final String params) {
				try {
					// Thread.sleep(3000);
					return loadAndCache(params);
				} catch (Exception e) {
					Log.e(TAG, "Unable to load", e);
					this.exception = e;
				}
				return null;

			}

			@Override
			protected void onPostExecute(final RESULT result) {
				if (exception != null) {
					if (exception instanceof MyenvocRemoteInvocationException) {
						MyenvocRemoteInvocationException myenvocException = (MyenvocRemoteInvocationException) exception;
						if (myenvocException.getFailure() != null) {
							callback.onError(myenvocException.getFailure());
						} else if (exception.getCause() instanceof ConnectTimeoutException
								|| exception.getCause() instanceof SocketTimeoutException) {
							callback.onError(Failure.byValue(FailureValue.CONNECTION_TIMEOUT));
						}
					} else {
						callback.onError(null);
					}
					return;
				}
				callback.onSuccess(result);
			};

		}, request);
	}

	/***
	 * Loads resource from database, safely throw exception, in case of DB
	 * error.
	 */
	protected abstract RESULT loadFromDataBase(String request);

	protected abstract void saveToDatabase(String request, RESULT result);

	protected abstract RESULT loadFromInternet(String request);

	protected abstract String getCacheKey(String request);

	private RESULT loadAndCache(final String url) {
		RESULT bitmap = load(url);
		GenericObjectCache.put(getCacheKey(url), bitmap);
		return bitmap;
	}

	private RESULT load(final String url) {
		RESULT result = loadFromDatabaseInternal(url);
		if (result != null) {
			return result;
		}
		RESULT remote = loadFromInternet(url);
		saveToDatabaseInternal(url, remote);
		return remote;
	}

	private RESULT loadFromDatabaseInternal(final String url) {
		try {
			RESULT fromDataBase = loadFromDataBase(url);
			if (fromDataBase != null) {
				Log.i(TAG, "Loaded resource from database: " + url);
			}
			return fromDataBase;
		} catch (Exception e) {
			Log.e(TAG, "Unable to load image from database", e);
		}
		return null;
	}

	private void saveToDatabaseInternal(final String url, final RESULT result) {
		if (result == null) {
			return;
		}
		try {
			saveToDatabase(url, result);
		} catch (Exception e) {
			Log.e(TAG, "Unable to save image", e);
		}

	}

}

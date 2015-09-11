package com.myenvoc.commons;

/**
 * Simple wrapper, allows to change implementation.
 * 
 * @param <Params>
 * @param <Result>
 */
public abstract class MyAsyncTask<Params, Result> {
	protected abstract Result doInBackgroundThread(Params param);

	/**
	 * Depending on executor implementation this may or may not be execute in UI
	 * thread. Client should decide between two options: 1) it needs to execute
	 * something in background and publish to UI 2) it needs to execute
	 * something, most likely on pool, but result will not be published to UI
	 * 
	 * @param result will be passed from doInBackgroundThread
	 */
	protected void onPostExecute(Result result) {

	}

}

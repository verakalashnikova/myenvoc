package com.myenvoc.commons;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.util.Log;

import com.myenvoc.commons.ExecutorFactory.ExecutorType;

public class ThreadPoolExecutor extends MyTaskExecutor {

	private final String TAG = ThreadPoolExecutor.class.getName();

	public static final ExecutorParameters TP_EXECUTOR_DEFAULT_PARAMETERS = new ThreadPoolExecutorParameters(ExecutorType.POOLED, -1, null);

	/**
	 * public static final ExecutorParameters TP_EXECUTOR_TIMED = new
	 * ThreadPoolExecutorParameters(ExecutorType.POOLED, 3000, null);
	 */

	public static volatile ExecutorParameters executorInUIThread;

	private final ExecutorService executor = Executors.newFixedThreadPool(3);

	public static ExecutorParameters getCallbackInUIThreadParameters(final Handler handler) {
		if (executorInUIThread == null) {
			synchronized (ThreadPoolExecutor.class) {
				if (executorInUIThread != null) {
					return executorInUIThread;
				}
				executorInUIThread = new ThreadPoolExecutorParameters(ExecutorType.POOLED, -1, handler);
			}
		}
		return executorInUIThread;
	}

	@Override
	public <Params, Result> void execute(final ExecutorParameters executorParameters, final MyAsyncTask<Params, Result> task,
			final Params... p) {

		ThreadPoolExecutorParameters threadPoolExecutorParameters = (ThreadPoolExecutorParameters) executorParameters;
		int timeout = threadPoolExecutorParameters.getTimeout();
		if (timeout > 0) {
			/**
			 * FIXME: currently execute regular execute and rely on
			 * ConnectionTimeout, etc. As wasn't able to find suitable way to
			 * execute trask with timeout. Future.get() is problematic.
			 */
			execute(task, threadPoolExecutorParameters, p);
		} else {
			execute(task, threadPoolExecutorParameters, p);
		}
	}

	private <Result, Params> void execute(final MyAsyncTask<Params, Result> task, final ThreadPoolExecutorParameters executorParams,
			final Params... p) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				final Result result = task.doInBackgroundThread(p[0]);
				if (executorParams.getHandler() == null) {
					task.onPostExecute(result);
				} else {
					executorParams.getHandler().post(new Runnable() {
						@Override
						public void run() {
							task.onPostExecute(result);
						}
					});
				}
			}
		});
	}

	private <Params, Result> void executeTimed(final MyAsyncTask<Params, Result> task, final int timeout,
			final ThreadPoolExecutorParameters executorParams, final Params... p) {
		try {

			executor.invokeAny(Collections.singletonList(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					Result result = task.doInBackgroundThread(p[0]);
					task.onPostExecute(result);
					return null;
				}

			}), timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Log.i(TAG, "Unable to execute task", e);
		}
	}

	private static class ThreadPoolExecutorParameters extends ExecutorParameters {

		private final int timeout;
		private final Handler handler;

		public ThreadPoolExecutorParameters(final ExecutorType executorType, final int timeout, final Handler handler) {
			super(executorType);
			this.timeout = timeout;
			this.handler = handler;
		}

		public int getTimeout() {
			return timeout;
		}

		public Handler getHandler() {
			return handler;
		}

	}

}

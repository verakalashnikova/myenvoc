package com.myenvoc.commons;

import android.os.AsyncTask;

import com.myenvoc.commons.ExecutorFactory.ExecutorType;

public class AndroidAsyncTaskExecutor extends MyTaskExecutor {

	public static final ExecutorParameters ANDROID_ASYNC_EXECUTOR_PARAMETERS = new ExecutorParameters(ExecutorType.ANDROID);
	@Override
	public <Params, Result> void execute(ExecutorParameters executorParameters, MyAsyncTask<Params, Result> task, Params... p) {
		executeAndroidAsyncTask(task, p);
		
	}

	private <Params, T, Result> void executeAndroidAsyncTask(final MyAsyncTask<Params, Result> task, Params... p) {
		new AsyncTask<Params, T, Result>() {

			@Override
			protected Result doInBackground(Params... params) {				
				return task.doInBackgroundThread(params[0]);
			}
			
			@Override
			protected void onPostExecute(Result result) {
				task.onPostExecute(result);
			};
			
		}.execute(p);
	}

	
	
}

package com.myenvoc.commons;

import com.myenvoc.commons.ExecutorFactory.ExecutorType;

public class SerialMyTaskExecutor extends MyTaskExecutor {

	public static final ExecutorParameters SERIAL_EXECUTOR_PARAMETERS = new ExecutorParameters(ExecutorType.SERIAL);

	@Override
	public <Params, Result> void execute(final ExecutorParameters executorParameters, final MyAsyncTask<Params, Result> task,
			final Params... p) {
		Result result = task.doInBackgroundThread(p[0]);
		task.onPostExecute(result);
	}

}

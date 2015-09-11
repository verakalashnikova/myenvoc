package com.myenvoc.commons;

import com.myenvoc.commons.ExecutorFactory.ExecutorType;

public abstract class MyTaskExecutor {


	public abstract <Params, Result> void execute(ExecutorParameters executorParameters, MyAsyncTask<Params, Result> task, Params... p);

	public static class ExecutorParameters {
		private final ExecutorType executorType;

		public ExecutorParameters(ExecutorType executorType) {
			super();
			this.executorType = executorType;
		}

		public ExecutorType getExecutorType() {
			return executorType;
		}
		
	}
}

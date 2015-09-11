package com.myenvoc.commons;

import java.util.Map;

import com.myenvoc.commons.MyTaskExecutor.ExecutorParameters;

public class ExecutorFactory {
	public enum ExecutorType {
		ANDROID, SERIAL, POOLED
	}
	
	public static ExecutorFactory instance;
	private Map<ExecutorType, MyTaskExecutor> config;
	private MyTaskExecutor overrideExecutorType;
	
	public static ExecutorFactory getInstance() {
		return instance;
	}
	
	public ExecutorFactory(Map<ExecutorType, MyTaskExecutor> config, ExecutorType overrideExecutorType) {
		this.config = config;
		if (overrideExecutorType != null) {
			this.overrideExecutorType = config.get(overrideExecutorType);
		}		
	}
	
	public <Params, Result> void execute(ExecutorParameters executorParameters, MyAsyncTask<Params, Result> task, Params... p) {
		MyTaskExecutor executor = getExecutorInternal(executorParameters);
		executor.execute(executorParameters, task, p);
	}
	

	private MyTaskExecutor getExecutorInternal(ExecutorParameters executorParameters) {
		if (overrideExecutorType != null) {
			return overrideExecutorType;
		}
		return config.get(executorParameters.getExecutorType());
	}

}

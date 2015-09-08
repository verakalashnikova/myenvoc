package com.myenvoc.android.inject;

import javax.inject.Inject;

import android.content.Context;

import com.google.inject.Provider;

public class ContextAware {
	@Inject
	private static Provider<Context> contextProvider;
		
	public static Context getContext() {
		return contextProvider.get();
	}
	
	public Context getContextFromInstance() {
		return contextProvider.get();
	}	
}

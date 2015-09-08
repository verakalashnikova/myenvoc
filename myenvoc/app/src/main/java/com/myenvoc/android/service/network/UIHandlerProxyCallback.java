package com.myenvoc.android.service.network;

import android.os.Handler;

import com.myenvoc.android.domain.Failure;

public class UIHandlerProxyCallback<D> implements GenericCallback<D> {

	private final GenericCallback<D> target;
	private final Handler handler;

	public UIHandlerProxyCallback(final GenericCallback<D> target, final Handler handler) {
		super();
		this.target = target;
		this.handler = handler;
	}

	@Override
	public void onSuccess(final D data) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				target.onSuccess(data);
			}
		});

	}

	@Override
	public void onError(final Failure failure) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				target.onError(failure);
			}
		});
	}

}

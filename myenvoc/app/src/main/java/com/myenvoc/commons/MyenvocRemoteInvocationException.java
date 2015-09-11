package com.myenvoc.commons;

import com.myenvoc.android.domain.Failure;

public class MyenvocRemoteInvocationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Failure failure;

	public MyenvocRemoteInvocationException(final Failure failure) {
		super();
		this.failure = failure;
	}

	public MyenvocRemoteInvocationException(final String string) {
		super(string);
	}

	public MyenvocRemoteInvocationException(final String string, final Exception e) {
		super(string, e);
	}

	public MyenvocRemoteInvocationException(final String string, final Failure failure) {
		super(string);
		this.failure = failure;
	}

	public MyenvocRemoteInvocationException(final String string, final Failure failure, final Exception cause) {
		super(string, cause);
		this.failure = failure;
	}

	public Failure getFailure() {
		return failure;
	}

	public void setFailure(final Failure failure) {
		this.failure = failure;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " " + failure;
	}

}

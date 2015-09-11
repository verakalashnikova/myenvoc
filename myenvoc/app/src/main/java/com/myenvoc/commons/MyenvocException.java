package com.myenvoc.commons;


public class MyenvocException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public MyenvocException(String string) {
		super(string);
	}
	public MyenvocException(String string, Exception e) {
		super(string, e);
	}
	public MyenvocException(Exception e) {
		super(e);
	}


}

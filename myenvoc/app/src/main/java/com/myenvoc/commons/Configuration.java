package com.myenvoc.commons;

public class Configuration {
	// http://10.0.2.2:8080/myenvoc-webapp/
	// http://192.168.1.8:8080/myenvoc-webapp/
	public final static String myenvoc_host = "http://myenvoc.com/";

	public final static String myenvocOpenID = "android-openid-login";

	public final static String myenvocPronounce = "api/word/pronounce?format=mp3&word=";

	public final static String pronounceResource = myenvoc_host + myenvocPronounce;
}

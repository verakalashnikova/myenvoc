package com.myenvoc.commons;

public class StringUtils {
	public static boolean isEmpty(String s) {
		return s == null || s.trim().equals("");
	}
	
	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}
}

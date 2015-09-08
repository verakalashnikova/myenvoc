package com.myenvoc.android.optimization;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class GenericObjectCache {
	private static Cache<String, Object> cache = CacheBuilder.newBuilder().maximumSize(20).build();

	public static Object get(final String key) {
		return cache.getIfPresent(key);
	}

	public static void put(final String key, final Object value) {
		if (key != null && value != null) {
			cache.put(key, value);
		}
	}
}

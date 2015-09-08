package com.myenvoc.android.dao.dictionary;

public enum VocabularyOrder {
	ADDED_DATE("ADDED_DATE desc"), LEMMA("LEMMA"), /** no particular order. */RANDOM(null);

	private String sql;

	private VocabularyOrder(final String sql) {
		this.sql = sql;
	}

	public String sql() {
		return sql;
	}
}

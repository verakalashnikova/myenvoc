package com.myenvoc.android.ui.dictionary;

import javax.inject.Inject;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.myenvoc.android.dao.dictionary.MyenvocDictionary;

public class MyenvocDictionaryLemmasProvider extends ContentProvider {

	private static final int SEARCH_SUGGEST = 1;

	@Inject
	private MyenvocDictionary myenvocDictionary;

	public static String AUTHORITY = MyenvocDictionaryLemmasProvider.class.getName();

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/dictionary");

	private static final UriMatcher sURIMatcher = buildUriMatcher();

	/**
	 * Builds up a UriMatcher for search suggestion and shortcut refresh
	 * queries.
	 */
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

		return matcher;
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs,
			final String sortOrder) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getSuggestions(selectionArgs[0]);
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	private Cursor getSuggestions(final String query) {
		String[] columns = new String[] { BaseColumns._ID, MyenvocDictionary.LEMMA, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		return myenvocDictionary.getSuggestLemmas(query.toLowerCase(), columns);
	}

	@Override
	public String getType(final Uri uri) {
		switch (sURIMatcher.match(uri)) {

		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreate() {
		return false;
	}

}

package com.myenvoc.android.dao.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.inject.Inject;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.common.base.Stopwatch;
import com.myenvoc.R;
import com.myenvoc.android.inject.ContextAware;

public class MyenvocDictionary extends SQLiteOpenHelper {
	private static final String INSERT_INTO_MYENVOC_LEMMA_SQL = "INSERT INTO MYENVOC_LEMMA VALUES(?)";

	private static final int BATCH_SIZE = 1000;

	private static final String TAG = MyenvocDictionary.class.toString();

	private static final String DATABASE_NAME = "myenvocdictionary";
	private static final String MYENVOC_LEMMAS_TABLE = "MYENVOC_LEMMA";
	private static final int DATABASE_VERSION = 2;
	public static final String LEMMA = SearchManager.SUGGEST_COLUMN_TEXT_1;

	private final Context context;

	private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE " + MYENVOC_LEMMAS_TABLE + " USING fts3 (" + LEMMA + ");";

	private static final HashMap<String, String> MYENVOC_LEMMAS_COLUMN_MAP = buildColumnMap();

	/**
	 * Builds a map for all columns that may be requested, which will be given
	 * to the SQLiteQueryBuilder. This is a good way to define aliases for
	 * column names, but must include all columns, even if the value is the key.
	 * This allows the ContentProvider to request columns w/o the need to know
	 * real column names and create the alias itself.
	 */
	private static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(LEMMA, LEMMA);

		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, LEMMA + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, SearchManager.SUGGEST_COLUMN_TEXT_1);
		return map;
	}

	@Inject
	public MyenvocDictionary(final ContextAware context) {
		super(context.getContextFromInstance(), DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context.getContextFromInstance();
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		super.onOpen(db);

		boolean lemmasLoaded = isDoneLoadingLemmas();
		if (!lemmasLoaded) {
			Log.i(TAG, "Continue to load lemmas");
			loadDictionary(db);
		}
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {

		db.execSQL("CREATE TABLE MYENVOC_LEMMA (suggest_text_1 text)");
		db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS LEMMA_INDEX ON MYENVOC_LEMMA (suggest_text_1 ASC)");
	}

	private void loadDictionary(final SQLiteDatabase db) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					loadWords(db);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	private void loadWords(final SQLiteDatabase db) throws IOException {
		Log.d(TAG, "Loading words...");

		BufferedReader reader = null;

		try {
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + MYENVOC_LEMMAS_TABLE, null);
			int lemmasCount;
			if (cursor != null && cursor.moveToNext()) {
				lemmasCount = cursor.getInt(0);
			} else {
				Log.i(TAG, "Unable to query number of lemmas");
				return;
			}

			Log.i(TAG, lemmasCount + " lemmas loaded so far");
			final Resources resources = context.getResources();
			InputStream inputStream = resources.openRawResource(R.raw.lemmas);
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			int counter = 0;
			int transactionCounter = -1;
			// Stopwatch stopwatch = new Stopwatch();
			SQLiteStatement stmt = db.compileStatement(INSERT_INTO_MYENVOC_LEMMA_SQL);
			while ((line = reader.readLine()) != null) {
				if (counter < lemmasCount) {
					counter++;
					continue;
				}
				transactionCounter++;
				if (transactionCounter == 0) {
					db.beginTransaction();
					// stopwatch.start();
				}

				stmt.bindString(1, line);

				stmt.executeInsert();
				counter++;
				if (transactionCounter == BATCH_SIZE) {
					db.setTransactionSuccessful();
					db.endTransaction();
					transactionCounter = -1;
					Log.i(TAG, "batch done " + counter);
					// stopwatch.reset();
				}
			}
			if (transactionCounter >= 0) {
				db.setTransactionSuccessful();
				db.endTransaction();
			}

		} catch (Exception e) {
			Log.e(TAG, "Unable to load lemmas", e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		doneLoadingLemmas();
	}

	private void doneLoadingLemmas() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putBoolean("LEMMAS_LOADED", true);
		edit.commit();
		Log.d(TAG, "DONE loading lemmas.");
	}

	private boolean isDoneLoadingLemmas() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean("LEMMAS_LOADED", false);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + MYENVOC_LEMMAS_TABLE);
		onCreate(db);
	}

	public Cursor getSuggestLemmas(final String query, final String[] columns) {
		String selection = LEMMA + ">=? AND " + LEMMA + "<=?";// LEMMA +
																// " LIKE ?";
																// word >=
																// 'chicken '
																// AND word <
																// 'chicken z'
		String[] selectionArgs = new String[] { query, query + "z" };
		// String[] selectionArgs = new String[] { query + "%" };

		return query(selection, selectionArgs, columns);

	}

	private Cursor query(final String selection, final String[] selectionArgs, final String[] columns) {
		/*
		 * The SQLiteBuilder provides a map for all possible columns requested
		 * to actual columns in the database, creating a simple column alias
		 * mechanism by which the ContentProvider does not need to know the real
		 * column names
		 */
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(MYENVOC_LEMMAS_TABLE);
		builder.setProjectionMap(MYENVOC_LEMMAS_COLUMN_MAP);

		Stopwatch stopwatch = new Stopwatch().start();
		Cursor cursor = builder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, null, "7");
		stopwatch.stop();
		Log.i(TAG, "Suggestion fetch time: " + stopwatch);
		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}
}

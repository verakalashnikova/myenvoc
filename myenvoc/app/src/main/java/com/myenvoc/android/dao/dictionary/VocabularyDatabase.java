package com.myenvoc.android.dao.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.myenvoc.android.domain.AnonymousUser;
import com.myenvoc.android.domain.Entity;
import com.myenvoc.android.domain.GoolgeImageSearchResult;
import com.myenvoc.android.domain.JSONConverter;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.domain.MyWordAttributes;
import com.myenvoc.android.domain.MyWordSyncResult;
import com.myenvoc.android.domain.TranslationMeaning;
import com.myenvoc.android.domain.UserProfile;
import com.myenvoc.android.domain.WordNetDefinition;
import com.myenvoc.android.inject.ContextAware;
import com.myenvoc.android.service.user.User;
import com.myenvoc.commons.MyenvocException;

/**
 * 
 * adb shell under tools /data/data/com.myenvoc/databases
 */
public class VocabularyDatabase extends SQLiteOpenHelper {
	private static final String ANONYMOUS_USER_GUID = "ANONYM";

	private static final String TAG = VocabularyDatabase.class.getName();

	private static final String DATABASE_NAME = "vocabulary";
	private static final int DATABASE_VERSION = 2;
	private static final String MY_WORDS_TABLE = "MY_WORDS";
	private static final String REMOVED_MY_WORDS_TABLE = "REMOVED_MY_WORDS";
	private static final String IMAGES_CACHE_TABLE = "IMAGES_CACHE";
	private static final String USER_TABLE = "USER";
	private static final String DICT_CACHE_TABLE = "DICT_CACHE";
	private static final String TRANSLATION_CACHE_TABLE = "TRANSLATION_CACHE";

	private static final String KEY_COLUMN = "KEY";
	private static final String DATA_COLUMN = "DATA";

	private static final String MY_WORD_ID_COLUMN = "MY_WORD_ID";
	private static final String MY_WORD_COLUMN = "MY_WORD";
	private static final String LEMMA_COLUMN = "LEMMA";
	private static final String SERVER_ID_COLUMN = "SERVER_ID";
	private static final String LOCAL_UPDATE_COLUMN = "LOCAL_UPDATE";
	private static final String SERVER_VERSION_COLUMN = "SERVER_VERSION";
	private static final String USER_ID_COLUMN = "USER_ID";

	private static final String DEFINITION_COLUMN = "DEFINITITION";
	private static final String TRANSLATION_COLUMN = "TRANSLATION";
	private static final String IMAGE_COLUMN = "IMAGE";
	private static final String WORD_COLUMN = "WORD";
	private static final String GUID_COLUMN = "GUID";
	private static final String CURRENT_COLUMN = "CURRENT";
	private static final String DATE_ADDED_COLUMN = "ADDED_DATE";

	private static final String[] DEFINITION_COLUMN_PROJECTION = new String[] { DEFINITION_COLUMN };
	private static final String[] TRANSLATION_COLUMN_PROJECTION = new String[] { TRANSLATION_COLUMN };
	private static final String[] IMAGE_COLUMN_PROJECTION = new String[] { IMAGE_COLUMN };
	private static final String[] USER_TABLE_COLUMN_PROJECTION = new String[] { BaseColumns._ID, DATA_COLUMN, GUID_COLUMN };
	private static final String[] IMAGES_TABLE_COLUMN_PROJECTION = new String[] { DATA_COLUMN };
	private static final String WORD_COLUMN_SELECTION = WORD_COLUMN + " = ?";

	private static final String[] MY_WORDS_ATTRIBUTES_COLUMNS = new String[] { BaseColumns._ID, SERVER_ID_COLUMN, SERVER_VERSION_COLUMN,
			LOCAL_UPDATE_COLUMN, MY_WORD_COLUMN };

	private static final String MY_WORDS_TABLE_CREATE_SQL = "CREATE TABLE " + MY_WORDS_TABLE + " (" + BaseColumns._ID
			+ " INTEGER PRIMARY KEY, " + USER_ID_COLUMN + " INTEGER, " + MY_WORD_COLUMN + " TEXT, " + LEMMA_COLUMN + " TEXT, "
			+ DATE_ADDED_COLUMN + " INTEGER, " + SERVER_ID_COLUMN + " INTEGER, " + SERVER_VERSION_COLUMN + " INTEGER, "
			+ LOCAL_UPDATE_COLUMN + " INTEGER);";

	private static final String REMOVED_MY_WORDS_TABLE_CREATE_SQL = "CREATE TABLE " + REMOVED_MY_WORDS_TABLE + " (" + MY_WORD_ID_COLUMN
			+ " INTEGER," + SERVER_ID_COLUMN + " INTEGER, " + USER_ID_COLUMN + " INTEGER);";

	private static final String IMAGES_CACHE_TABLE_CREATE_SQL = "CREATE TABLE " + IMAGES_CACHE_TABLE + " (" + KEY_COLUMN + " TEXT,"
			+ DATA_COLUMN + " BLOB);";

	private static final String USER_TABLE_CREATE_SQL = "CREATE TABLE " + USER_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY, "
			+ GUID_COLUMN + " TEXT," + DATA_COLUMN + " TEXT" + ", " + CURRENT_COLUMN + " INTEGER );";

	private static final String DICT_CACHE_CREATE_SQL = "CREATE TABLE " + DICT_CACHE_TABLE + " (" + WORD_COLUMN + " TEXT,"
			+ DEFINITION_COLUMN + " TEXT," + IMAGE_COLUMN + " TEXT" + " );";

	private static final String TRANSLATION_CACHE_CREATE_SQL = "CREATE TABLE " + TRANSLATION_CACHE_TABLE + " (" + WORD_COLUMN + " TEXT,"
			+ TRANSLATION_COLUMN + " TEXT);";

	private static final Function<String, Character> QUESTION_MARK_FUNCTION = new Function<String, Character>() {
		@Override
		public Character apply(final String input) {
			return '?';
		}
	};

	@Inject
	public VocabularyDatabase(final ContextAware contextAware) {
		super(contextAware.getContextFromInstance(), DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL(USER_TABLE_CREATE_SQL);

		ContentValues values = new ContentValues();
		values.put(GUID_COLUMN, ANONYMOUS_USER_GUID);
		values.put(CURRENT_COLUMN, 1);
		db.insert(USER_TABLE, null, values);

		db.execSQL(MY_WORDS_TABLE_CREATE_SQL);
		db.execSQL(REMOVED_MY_WORDS_TABLE_CREATE_SQL);

		/** Common for all users: */
		db.execSQL(IMAGES_CACHE_TABLE_CREATE_SQL);
		db.execSQL(DICT_CACHE_CREATE_SQL);
		db.execSQL(TRANSLATION_CACHE_CREATE_SQL);

	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		// TODO: implement
		System.out.println("upgrade");
	}

	public MyWord addMyWord(final MyWord myWord, final int serverId, final int serverVersion, final boolean localUpdate, final User user) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(MY_WORD_COLUMN, JSONConverter.convertToString(myWord));
		initialValues.put(SERVER_ID_COLUMN, serverId);
		initialValues.put(SERVER_VERSION_COLUMN, serverVersion);
		initialValues.put(LOCAL_UPDATE_COLUMN, booleanToInt(localUpdate));
		initialValues.put(LEMMA_COLUMN, myWord.getLemma());
		initialValues.put(DATE_ADDED_COLUMN, myWord.getAdded() == null ? 0 : myWord.getAdded().getTime());
		initialValues.put(USER_ID_COLUMN, user.getId());

		long rawId = getWritableDatabase().insert(MY_WORDS_TABLE, null, initialValues);

		int id = throwIfError(rawId);
		myWord.getAttributes().setId(id);
		myWord.getAttributes().setServerId(serverId);
		myWord.getAttributes().setServerVersion(serverVersion);

		return myWord;
	}

	private static int booleanToInt(final boolean localUpdate) {
		return localUpdate ? 1 : 0;
	}

	private static int throwIfError(final long id) {
		if (id == -1) {
			throw new MyenvocException("Can't execute insert");
		}
		return (int) id;
	}

	public void updateMyWordAttributes(final int clientId, final int serverId, final int serverVersion, final boolean localUpdate) {
		ContentValues values = new ContentValues();
		values.put(SERVER_ID_COLUMN, serverId);
		values.put(SERVER_VERSION_COLUMN, serverVersion);
		values.put(LOCAL_UPDATE_COLUMN, booleanToInt(localUpdate));
		getWritableDatabase().update(MY_WORDS_TABLE, values, idWhereClause(), intArg(clientId));

	}

	public void removeMyWord(final MyWordAttributes myWordAttributes, final User user) {
		getWritableDatabase().delete(MY_WORDS_TABLE, idWhereClause(), intArg(myWordAttributes.getId()));

		if (!user.isAnonymous()) {
			ContentValues values = new ContentValues();
			values.put(MY_WORD_ID_COLUMN, myWordAttributes.getId());
			values.put(SERVER_ID_COLUMN, myWordAttributes.getServerId());
			values.put(USER_ID_COLUMN, user.getId());
			getWritableDatabase().insert(REMOVED_MY_WORDS_TABLE, null, values);
		}
	}

	public void removeMyWordByServerId(final int serverId) {
		getWritableDatabase().delete(MY_WORDS_TABLE, String.valueOf(SERVER_ID_COLUMN) + "=?", intArg(serverId));
	}

	private String idWhereClause() {
		return String.valueOf(BaseColumns._ID) + "=?";
	}

	private String[] intArg(final int clientId) {
		return new String[] { String.valueOf(clientId) };
	}

	public void updateMyWord(final int clientId, final MyWord myWord, final int serverId, final int serverVersion, final boolean localUpdate) {
		ContentValues values = new ContentValues();
		values.put(MY_WORD_COLUMN, JSONConverter.convertToString(myWord));
		if (serverId > 0) {
			values.put(SERVER_ID_COLUMN, serverId);
		}
		if (serverVersion > 0) {
			values.put(SERVER_VERSION_COLUMN, serverVersion);
		}
		values.put(LOCAL_UPDATE_COLUMN, booleanToInt(localUpdate));
		getWritableDatabase().update(MY_WORDS_TABLE, values, idWhereClause(), intArg(clientId));
	}

	public void updateMyWordByServerId(final MyWord myWord, final int serverId, final int serverVersion) {
		ContentValues values = new ContentValues();
		values.put(MY_WORD_COLUMN, JSONConverter.convertToString(myWord));
		values.put(SERVER_VERSION_COLUMN, serverVersion);
		values.put(LOCAL_UPDATE_COLUMN, booleanToInt(false));

		getWritableDatabase().update(MY_WORDS_TABLE, values, String.valueOf(SERVER_ID_COLUMN) + "=?", intArg(serverId));
	}

	public Cursor findAllMyWordsRaw(final User user, final VocabularyOrder vocabularyOrder) {

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(MY_WORDS_TABLE);

		String order = vocabularyOrder == null ? null : vocabularyOrder.sql();
		return builder.query(getReadableDatabase(), MY_WORDS_ATTRIBUTES_COLUMNS, USER_ID_COLUMN + " = ? ",
				new String[] { String.valueOf(user.getId()) }, null, null, order);
	}

	public Cursor findAllRemovedMyWordsRaw(final User user) {

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(REMOVED_MY_WORDS_TABLE);
		return builder.query(getReadableDatabase(), new String[] { MY_WORD_ID_COLUMN, SERVER_ID_COLUMN }, USER_ID_COLUMN + " = ? ",
				new String[] { String.valueOf(user.getId()) }, null, null, null);
	}

	public void confirmMyWordsRemove(final List<MyWordSyncResult> list) {

		List<String> confirmedRemoves = Lists.newArrayList(Iterables.transform(list, new Function<MyWordSyncResult, String>() {

			@Override
			public String apply(final MyWordSyncResult res) {
				return String.valueOf(res.getClientId());
			}

		}));

		String questions = Joiner.on(',').join(Iterables.transform(confirmedRemoves, QUESTION_MARK_FUNCTION));

		getWritableDatabase().delete(REMOVED_MY_WORDS_TABLE, MY_WORD_ID_COLUMN + " IN (" + questions + ")",
				confirmedRemoves.toArray(new String[confirmedRemoves.size()]));
	}

	public Collection<MyWord> findAllMyWords(final User user, final VocabularyOrder vocabularyOrder) {
		// Stopwatch stopwatch = new Stopwatch().start();
		Cursor cursor = findAllMyWordsRaw(user, vocabularyOrder);
		if (cursor == null) {
			return Collections.emptyList();
		}
		// stopwatch.stop();
		// Log.i(TAG, "fetch time " + stopwatch);
		// stopwatch.reset().start();

		List<MyWord> words = new ArrayList<MyWord>();
		while (cursor.moveToNext()) {
			words.add(createMyWord(cursor));
		}
		// Log.i(TAG, "convert time " + stopwatch.stop());
		cursor.close();
		return words;
	}

	public MyWord findMyWord(final String lemma, final User user) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(MY_WORDS_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), MY_WORDS_ATTRIBUTES_COLUMNS, LEMMA_COLUMN + "=? AND " + USER_ID_COLUMN
				+ " = ? ", new String[] { lemma, String.valueOf(user.getId()) }, null, null, null);
		if (cursor == null || !cursor.moveToNext()) {
			return null;
		}
		try {
			return createMyWord(cursor);
		} finally {
			cursor.close();
		}
	}

	public MyWord findMyWordById(final int myWordId) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(MY_WORDS_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), MY_WORDS_ATTRIBUTES_COLUMNS, BaseColumns._ID + "=?",
				new String[] { String.valueOf(myWordId) }, null, null, null);
		if (cursor == null || !cursor.moveToNext()) {
			return null;
		}
		try {
			return createMyWord(cursor);
		} finally {
			cursor.close();
		}
	}

	private MyWord createMyWord(final Cursor cursor) {
		int clientId = cursor.getInt(0);
		int serverId = cursor.getInt(1);
		int serverVersion = cursor.getInt(2);
		// int updatedLocally = cursor.getInt(3);
		String asString = cursor.getString(4);

		MyWord myWord = JSONConverter.loadSingleObject(asString, MyWord.class);

		myWord.getAttributes().setId(clientId);
		myWord.getAttributes().setServerId(serverId);
		myWord.getAttributes().setServerVersion(serverVersion);
		return myWord;
	}

	public void insertImage(final String url, final Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] photo = baos.toByteArray();

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_COLUMN, url);
		initialValues.put(DATA_COLUMN, photo);
		getWritableDatabase().insert(IMAGES_CACHE_TABLE, null, initialValues);
	}

	public Bitmap findImage(final String url) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(IMAGES_CACHE_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), IMAGES_TABLE_COLUMN_PROJECTION, KEY_COLUMN + " = ?", new String[] { url },
				null, null, null);

		if (cursor != null) {
			if (cursor.moveToNext()) {
				byte[] blob = cursor.getBlob(0);

				if (blob != null) {
					ByteArrayInputStream imageStream = new ByteArrayInputStream(blob);
					return BitmapFactory.decodeStream(imageStream);
				}
			}
			cursor.close();
		}

		return null;
	}

	public AnonymousUser findAnonymousUser() {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(USER_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), new String[] { BaseColumns._ID }, GUID_COLUMN + " = ?",
				new String[] { ANONYMOUS_USER_GUID }, null, null, null);

		if (cursor != null) {
			try {
				if (cursor.moveToNext()) {
					return new AnonymousUser(cursor.getInt(0));
				}
			} catch (Exception e) {
				Log.e(TAG, "unable to get user", e);
			} finally {
				cursor.close();
			}
		}
		return null;

	}

	public UserProfile findUserProfile(final String guid) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(USER_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), USER_TABLE_COLUMN_PROJECTION, GUID_COLUMN + " = ?", new String[] { guid },
				null, null, null);

		if (cursor != null) {
			try {
				if (cursor.moveToNext()) {
					UserProfile userProfile = JSONConverter.loadSingleObject(cursor.getString(1), UserProfile.class);
					userProfile.setId(cursor.getInt(0));
					return userProfile;
				}
			} catch (Exception e) {
				Log.e(TAG, "unable to get user", e);
			} finally {
				cursor.close();
			}
		}

		return null;
	}

	public void saveOrUpdateUser(final UserProfile user) {
		ContentValues values = new ContentValues();
		values.put(GUID_COLUMN, user.getGuid());
		values.put(DATA_COLUMN, JSONConverter.convertToString(user));

		int updated = getWritableDatabase().update(USER_TABLE, values, GUID_COLUMN + " = ?", new String[] { user.getGuid() });

		if (updated == 1) {
			return;
		}

		getWritableDatabase().insert(USER_TABLE, null, values);
	}

	public void setCurrentUser(final User user) {
		ContentValues values = new ContentValues();
		values.put(CURRENT_COLUMN, 0);

		getWritableDatabase().update(USER_TABLE, values, null, null);

		values.put(CURRENT_COLUMN, 1);
		getWritableDatabase().update(USER_TABLE, values, BaseColumns._ID + " = ?", new String[] { String.valueOf(user.getId()) });
	}

	public User findCurrentUser() {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(USER_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), USER_TABLE_COLUMN_PROJECTION, CURRENT_COLUMN + " = ?",
				new String[] { String.valueOf(1) }, null, null, null);

		if (cursor != null) {
			try {
				if (cursor.moveToNext()) {
					if (ANONYMOUS_USER_GUID.equals(cursor.getString(2))) {
						return new AnonymousUser(cursor.getInt(0));
					}
					UserProfile userProfile = JSONConverter.loadSingleObject(cursor.getString(1), UserProfile.class);
					userProfile.setId(cursor.getInt(0));
					return userProfile;
				}
			} catch (Exception e) {
				Log.e(TAG, "unable to get user", e);
			} finally {
				cursor.close();
			}
		}
		throw new RuntimeException("Can't find current user");
	}

	public WordNetDefinition findDefinition(final String word) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(DICT_CACHE_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), DEFINITION_COLUMN_PROJECTION, WORD_COLUMN_SELECTION, new String[] { word },
				null, null, null);
		return loadSingleObject(cursor, WordNetDefinition.class);
	}

	public void saveDefinition(final String word, final WordNetDefinition result) {
		ContentValues values = new ContentValues();
		values.put(DEFINITION_COLUMN, JSONConverter.convertToString(result));

		int updated = getWritableDatabase().update(DICT_CACHE_TABLE, values, WORD_COLUMN_SELECTION, new String[] { word });

		if (updated == 1) {
			return;
		}
		values.put(WORD_COLUMN, word);

		getWritableDatabase().insert(DICT_CACHE_TABLE, null, values);
	}

	public Collection<TranslationMeaning> findTranslation(final String word) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(TRANSLATION_CACHE_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), TRANSLATION_COLUMN_PROJECTION, WORD_COLUMN_SELECTION, new String[] { word },
				null, null, null);
		return loadCollection(cursor, TranslationMeaning.class);
	}

	public void saveTranslation(final String word, final Collection<TranslationMeaning> result) {
		ContentValues values = new ContentValues();
		values.put(TRANSLATION_COLUMN, JSONConverter.convertCollectionToString(result));

		int updated = getWritableDatabase().update(TRANSLATION_CACHE_TABLE, values, WORD_COLUMN_SELECTION, new String[] { word });

		if (updated == 1) {
			return;
		}
		values.put(WORD_COLUMN, word);

		getWritableDatabase().insert(TRANSLATION_CACHE_TABLE, null, values);

	}

	public GoolgeImageSearchResult findImageSearch(final String word) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(DICT_CACHE_TABLE);
		Cursor cursor = builder.query(getReadableDatabase(), IMAGE_COLUMN_PROJECTION, WORD_COLUMN_SELECTION, new String[] { word }, null,
				null, null);
		return loadSingleObject(cursor, GoolgeImageSearchResult.class);
	}

	public void saveImageSearch(final String word, final GoolgeImageSearchResult result) {
		ContentValues values = new ContentValues();
		values.put(IMAGE_COLUMN, JSONConverter.convertToString(result));

		int updated = getWritableDatabase().update(DICT_CACHE_TABLE, values, WORD_COLUMN_SELECTION, new String[] { word });

		if (updated == 1) {
			return;
		}
		values.put(WORD_COLUMN, word);

		getWritableDatabase().insert(DICT_CACHE_TABLE, null, values);

	}

	private <T extends Entity> Collection<T> loadCollection(final Cursor cursor, final Class<T> clazz) {
		if (cursor != null && cursor.moveToNext()) {
			String string = cursor.getString(0);
			if (string == null) {
				return null;
			}
			return JSONConverter.loadCollection(string, clazz);
		}
		return null;
	}

	private <T extends Entity> T loadSingleObject(final Cursor cursor, final Class<T> clazz) {
		if (cursor != null && cursor.moveToNext()) {
			String string = cursor.getString(0);
			if (string == null) {
				return null;
			}
			return JSONConverter.loadSingleObject(string, clazz);
		}
		return null;
	}

	public void clearMyWordsTables() {
		getWritableDatabase().delete(MY_WORDS_TABLE, null, null);
		getWritableDatabase().delete(REMOVED_MY_WORDS_TABLE, null, null);
	}

}

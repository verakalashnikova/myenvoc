package com.myenvoc.android.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.myenvoc.commons.MyenvocException;

public class JSONConverter {
	private static final String TAG = JSONConverter.class.getName();

	public static <E extends Entity> Collection<E> loadCollection(final String feed, final Class<? extends E> clazz) {
		try {
			JSONArray jsonArray = new JSONArray(feed);

			Collection<E> result = new ArrayList<E>(jsonArray.length());

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);

				result.add(loadSingleObject(object, clazz));
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Unable to load: " + clazz, e);
		}
	}

	public static <E extends Entity> E loadSingleObject(final String feed, final Class<E> clazz) {
		JSONObject json;
		try {
			json = new JSONObject(feed);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load: " + clazz, e);
		}
		return loadSingleObject(json, clazz);
	}

	@SuppressWarnings("unchecked")
	public static <E extends Entity> E loadSingleObject(final JSONObject json, final Class<E> clazz) {
		try {
			return (E) clazz.newInstance().load(json);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load: " + clazz, e);
		}
	}

	public static String convertToString(final Entity entity) {
		try {
			return entity.convertToJson().toString();
		} catch (JSONException e) {
			throw new MyenvocException("Unable to convert to string", e);
		}
	}

	public static String convertArrayToString(final Entity[] entities) {

		try {
			JSONArray array = new JSONArray();
			for (Entity entity : entities) {
				array.put(entity.convertToJson());
			}
			return array.toString();
		} catch (JSONException e) {
			throw new MyenvocException("Unable to convert to string", e);
		}
	}

	public static String convertCollectionToString(final Collection<? extends Entity> entities) {
		try {
			JSONArray array = new JSONArray();
			for (Entity entity : entities) {
				array.put(entity.convertToJson());
			}
			return array.toString();
		} catch (JSONException e) {
			throw new MyenvocException("Unable to convert to string", e);
		}
	}
}

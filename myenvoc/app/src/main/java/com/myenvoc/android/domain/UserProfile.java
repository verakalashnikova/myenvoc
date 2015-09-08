package com.myenvoc.android.domain;

import org.json.JSONException;
import org.json.JSONObject;

import com.myenvoc.android.service.user.User;

/** AUTO-GENERATED **/
public class UserProfile implements Entity, User {

	private boolean openId;
	private String firstName;
	private String lastName;
	private String password;
	private String nativeLanguage;
	private String gender;
	private String email;
	private String guid;

	private int id;

	public boolean isOpenId() {
		return openId;
	}

	public void setOpenId(final boolean openId) {
		this.openId = openId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getNativeLanguage() {
		return nativeLanguage;
	}

	public void setNativeLanguage(final String nativeLanguage) {
		this.nativeLanguage = nativeLanguage;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	public UserProfile load(final JSONObject object) throws JSONException {

		this.firstName = object.optString("firstName");

		this.lastName = object.optString("lastName");

		this.password = object.optString("password");

		this.nativeLanguage = object.optString("nativeLanguage");

		this.gender = object.optString("gender");

		this.email = object.optString("email");

		this.guid = object.optString("guid");

		if (object.opt("openId") != null) {
			this.openId = (Boolean) object.get("openId");
		}

		return this;
	}

	@Override
	public JSONObject convertToJson() throws JSONException {
		JSONObject conversion = new JSONObject();
		conversion.put("openId", this.openId);

		if (this.firstName != null) {
			conversion.put("firstName", this.firstName);
		}

		if (this.lastName != null) {
			conversion.put("lastName", this.lastName);
		}

		if (this.password != null) {
			conversion.put("password", this.password);
		}

		if (this.nativeLanguage != null) {
			conversion.put("nativeLanguage", this.nativeLanguage);
		}

		if (this.gender != null) {
			conversion.put("gender", this.gender);
		}

		if (this.email != null) {
			conversion.put("email", this.email);
		}

		if (this.guid != null) {
			conversion.put("guid", this.guid);
		}

		return conversion;

	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}
}
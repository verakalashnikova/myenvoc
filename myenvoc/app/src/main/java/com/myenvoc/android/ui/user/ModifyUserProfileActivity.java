package com.myenvoc.android.ui.user;

import javax.inject.Inject;

import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.Languages;
import com.myenvoc.android.domain.UserProfile;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.user.User;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.ui.MyenvocActivity;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;
import com.myenvoc.android.ui.user.SignInActivity.SignInType;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyUserProfileActivity extends MyenvocActivity {

	@Inject
	UserService userService;

	TextView error;
	Spinner langSpinner;
	TextView email;
	TextView first;
	TextView last;
	Button modifyProfile;
	View inProgress;

	GenericCallback<UserProfile> modifyProfileCallback = new GenericCallback<UserProfile>() {

		@Override
		public void onSuccess(final UserProfile data) {
			Toast.makeText(ModifyUserProfileActivity.this.getBaseContext(), R.string.modifyUserProfileSuccess, Toast.LENGTH_LONG).show();

			Intent intent = new Intent(ModifyUserProfileActivity.this, MyenvocHomeActivity.class);
			startActivity(intent);
			ModifyUserProfileActivity.this.finish();
		}

		@Override
		public void onError(final Failure failure) {
			modifyProfile.setVisibility(View.VISIBLE);
			inProgress.setVisibility(View.GONE);

			SignInActivity.handleError(failure, error, ModifyUserProfileActivity.this.getBaseContext());
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		User user = userService.getUser();

		if (user.isAnonymous()) {
			Intent intent = new Intent(ModifyUserProfileActivity.this, SignInActivity.class);
			intent.putExtra(SignInType.TYPE, SignInType.SIGN_IN.name());
			startActivity(intent);
			finish();
			return;
		}

		UserProfile userProfile = userService.getUserProfile();
		if (userProfile.isOpenId()) {
			setContentView(R.layout.modify_profile_openid);

		} else {
			setContentView(R.layout.modify_profile_regular);

			// TextView password = (TextView) findViewById(R.id.password);
			// password.setText("");
		}

		langSpinner = (Spinner) findViewById(R.id.lang);
		SignInActivity.setupLangSpinner(langSpinner, getBaseContext());

		email = (TextView) findViewById(R.id.email);
		first = (TextView) findViewById(R.id.firstName);
		last = (TextView) findViewById(R.id.lastName);

		email.setText(userProfile.getEmail());
		first.setText(userProfile.getFirstName());
		last.setText(userProfile.getLastName());
		Languages lang = Languages.resolve(userProfile.getNativeLanguage());

		if (lang != null) {
			langSpinner.setSelection(lang.ordinal());
		}

		error = (TextView) findViewById(R.id.error);
		modifyProfile = (Button) findViewById(R.id.modifyProfile);
		inProgress = findViewById(R.id.inProgress);
	}

	public void onModifyRegular(final View view) {

		TextView password = (TextView) findViewById(R.id.password);
		String pwdValue = password.getText().toString();

		modifyProfile(pwdValue);

	}

	public void onModifyOpenId(final View view) {
		modifyProfile(null);
	}

	private void modifyProfile(final String pwdValue) {
		UserProfile userProfile = userService.getUserProfile();
		String guid = userProfile.getGuid();
		Languages language = (Languages) langSpinner.getSelectedItem();

		error.setVisibility(View.GONE);
		String emailValue = email.getText().toString();
		String firstValue = first.getText().toString();
		String lastValue = last.getText().toString();

		String lang = language == null || language.ordinal() == 0 ? null : language.getLang();

		modifyProfile.setVisibility(View.GONE);
		inProgress.setVisibility(View.VISIBLE);

		userService.modifyUserProfile(guid, emailValue, pwdValue, firstValue, lastValue, lang, MyenvocHomeActivity.getHandler(),
				getBaseContext(), modifyProfileCallback);

	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent vocabularyIntent = new Intent(this, MyenvocHomeActivity.class);
			startActivity(vocabularyIntent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simple_menu, menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}
}
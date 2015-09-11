package com.myenvoc.android.ui.user;

import java.util.Locale;

import javax.inject.Inject;

import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.FailureValue;
import com.myenvoc.android.domain.Languages;
import com.myenvoc.android.domain.UserProfile;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.network.NetworkService;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.ui.MyenvocActivity;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.Configuration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends MyenvocActivity {

	private static final String OPEN_ID_JS_OBJECT_NAME = "myenvocAndroid";
	@Inject
	UserService userService;

	@Inject
	NetworkService networkService;

	public enum SignInType {
		SIGN_IN, SIGN_UP, SIGN_IN_OPENID;
		public static final String TYPE = "SignInType";
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		SignInType signInType = SignInType.valueOf(intent.getExtras().getString(SignInType.TYPE));
		switch (signInType) {
		case SIGN_IN:
			setContentView(R.layout.sign_in);
			View emailTextView = findViewById(R.id.email);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(emailTextView.getWindowToken(), 0);
			break;
		case SIGN_UP:
			setContentView(R.layout.sign_up);
			Spinner langSpinner = (Spinner) findViewById(R.id.lang);
			setupLangSpinner(langSpinner, getBaseContext());

			tryToPresetLang(langSpinner);
			break;
		case SIGN_IN_OPENID:

			// CookieSyncManager cookieSyncManager =
			// CookieSyncManager.createInstance(getBaseContext());
			// CookieManager cookieManager = CookieManager.getInstance();
			// cookieManager.setAcceptCookie(true);
			// cookieManager.removeSessionCookie();
			//
			// cookieSyncManager.sync();
			final View openIdView = getLayoutInflater().inflate(R.layout.openid, null);
			final View progressBar = openIdView.findViewById(R.id.inProgress);

			WebView webView = (WebView) openIdView.findViewById(R.id.webView);

			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setUseWideViewPort(true);
			webView.setVerticalScrollBarEnabled(true);
			webView.setHorizontalScrollBarEnabled(true);

			webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
					progressBar.setVisibility(View.VISIBLE);
					super.onPageStarted(view, url, favicon);
				}

				@Override
				public void onPageFinished(final WebView view, final String url) {
					progressBar.setVisibility(View.GONE);
					super.onPageFinished(view, url);
				}
			});
			webView.addJavascriptInterface(new OpenIDCallbackInterface(getBaseContext(), MyenvocHomeActivity.getHandler(), userService,
					new GenericCallback<UserProfile>() {

						@Override
						public void onSuccess(final UserProfile data) {
							Toast.makeText(SignInActivity.this.getBaseContext(), R.string.signInSuccess, Toast.LENGTH_LONG).show();
							Intent intent = new Intent(SignInActivity.this, MyenvocHomeActivity.class);
							startActivity(intent);
						}

						@Override
						public void onError(final Failure failure) {
							final String message;
							if (failure == null) {
								message = FailureValue.GENERAL_ERROR.getLocalized(getBaseContext());
							} else {
								message = failure.getLocalized(getBaseContext());
							}

							Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

							Intent intent = new Intent(SignInActivity.this, SignInActivity.class);
							intent.putExtra(SignInType.TYPE, SignInType.SIGN_IN.name());
							startActivity(intent);
						}
					}), OPEN_ID_JS_OBJECT_NAME);

			// webView.setLayoutParams(new
			// LayoutParams(LayoutParams.WRAP_CONTENT,
			// LayoutParams.WRAP_CONTENT));
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(Configuration.myenvoc_host + Configuration.myenvocOpenID);

			setContentView(openIdView);
			break;
		default:
			throw new RuntimeException("Wrong intent");
		}
	}

	public static Spinner setupLangSpinner(final Spinner langSpinner, final Context context) {

		ArrayAdapter<Languages> adapter = new ArrayAdapter<Languages>(context, android.R.layout.simple_spinner_item, Languages.values());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		langSpinner.setAdapter(adapter);
		return langSpinner;
	}

	private void tryToPresetLang(final Spinner langSpinner) {
		if (Locale.getDefault() != null) {
			String defaultLang = Locale.getDefault().getLanguage();
			if (Languages.English.getLang().equalsIgnoreCase(defaultLang)) {
				return; // can't really detect
			}
			for (Languages language : Languages.values()) {
				if (defaultLang.equalsIgnoreCase(language.getLang())) {
					langSpinner.setSelection(language.ordinal());
					break;
				}

			}
		}
	}

	public void onSignIn(final View view) {
		if (!networkService.isConnected()) {
			noInternetToast();
			return;
		}
		TextView email = (TextView) findViewById(R.id.email);
		TextView password = (TextView) findViewById(R.id.password);
		final Button signIn = (Button) findViewById(R.id.signIn);

		signIn.setVisibility(View.GONE);
		final View inProgress = findViewById(R.id.inProgress);
		inProgress.setVisibility(View.VISIBLE);

		TextView error = (TextView) findViewById(R.id.error);
		error.setVisibility(View.GONE);

		userService.signIn(email.getText().toString(), password.getText().toString(), MyenvocHomeActivity.getHandler(), getBaseContext(),
				new GenericCallback<UserProfile>() {

					@Override
					public void onSuccess(final UserProfile data) {
						Toast.makeText(SignInActivity.this.getBaseContext(), R.string.signInSuccess, Toast.LENGTH_LONG).show();
						Intent intent = new Intent(SignInActivity.this, MyenvocHomeActivity.class);
						startActivity(intent);
						finish();
					}

					@Override
					public void onError(final Failure failure) {
						signIn.setVisibility(View.VISIBLE);
						inProgress.setVisibility(View.GONE);
						TextView error = (TextView) findViewById(R.id.error);
						handleError(failure, error, SignInActivity.this.getBaseContext());
					}
				});
	}

	public void onStartSignUp(final View view) {
		if (!networkService.isConnected()) {
			noInternetToast();
			return;
		}
		Intent intent = new Intent(this, SignInActivity.class);
		intent.putExtra(SignInType.TYPE, SignInType.SIGN_UP.name());
		startActivity(intent);
	}

	public void onStartSignInOpenID(final View view) {
		if (!networkService.isConnected()) {
			noInternetToast();
			return;
		}
		Intent intent = new Intent(this, SignInActivity.class);
		intent.putExtra(SignInType.TYPE, SignInType.SIGN_IN_OPENID.name());
		startActivity(intent);
	}

	private void noInternetToast() {
		Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
	}

	public void onSignUp(final View view) {
		TextView email = (TextView) findViewById(R.id.email);
		TextView password = (TextView) findViewById(R.id.password);
		TextView first = (TextView) findViewById(R.id.firstName);
		TextView last = (TextView) findViewById(R.id.lastName);
		Spinner langSpinner = (Spinner) findViewById(R.id.lang);
		final Button signUp = (Button) findViewById(R.id.signUp);

		TextView error = (TextView) findViewById(R.id.error);
		error.setVisibility(View.GONE);
		String emailValue = email.getText().toString();
		String pwdValue = password.getText().toString();
		String firstValue = first.getText().toString();
		String lastValue = last.getText().toString();
		Languages language = (Languages) langSpinner.getSelectedItem();

		String lang = language == null ? null : language.getLang();

		if (!validateSignUp(emailValue, pwdValue, firstValue, lastValue, lang, error)) {
			return;
		}
		signUp.setVisibility(View.GONE);
		final View inProgress = findViewById(R.id.inProgress);
		inProgress.setVisibility(View.VISIBLE);

		userService.signUp(emailValue, pwdValue, firstValue, lastValue, lang, MyenvocHomeActivity.getHandler(), getBaseContext(),
				new GenericCallback<UserProfile>() {

					@Override
					public void onSuccess(final UserProfile data) {
						Toast.makeText(SignInActivity.this.getBaseContext(), R.string.signUpSuccess, Toast.LENGTH_LONG).show();

						Intent intent = new Intent(SignInActivity.this, MyenvocHomeActivity.class);
						startActivity(intent);
						finish();
						// TODO: start vocabulary sync., do it on app start as
						// well
						// throw new RuntimeException("TODO");
					}

					@Override
					public void onError(final Failure failure) {
						signUp.setVisibility(View.VISIBLE);
						inProgress.setVisibility(View.GONE);
						TextView error = (TextView) findViewById(R.id.error);
						handleError(failure, error, SignInActivity.this.getBaseContext());

					}

				});
	}

	private boolean validateSignUp(final String emailValue, final String pwdValue, final String firstValue, final String lastValue,
			final String lang, final TextView error) {

		if (CommonUtils.isEmpty(emailValue) || !emailValue.matches(CommonUtils.EMAIL_REGEXP)) {
			error.setText(R.string.invalidEmail);
			error.setVisibility(View.VISIBLE);
			return false;
		}
		if (CommonUtils.isEmpty(pwdValue) || pwdValue.length() < 6) {
			error.setText(R.string.invalidPassword);
			error.setVisibility(View.VISIBLE);
			return false;
		}
		return true;
	}

	public static void handleError(final Failure failure, final TextView error, final Context context) {

		error.setVisibility(View.VISIBLE);

		if (failure == null) {
			error.setText(FailureValue.GENERAL_ERROR.getLocalized(context));
		} else {
			error.setText(failure.getLocalized(context));
		}
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

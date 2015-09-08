package com.myenvoc.android.ui.user;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.myenvoc.android.domain.UserProfile;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.user.UserService;

public class OpenIDCallbackInterface {

	private final Context context;
	private final UserService userService;
	private final GenericCallback<UserProfile> callback;
	private final Handler handler;

	public OpenIDCallbackInterface(final Context context, final Handler handler, final UserService userService,
			final GenericCallback<UserProfile> callback) {
		super();
		this.context = context;
		this.handler = handler;
		this.userService = userService;
		this.callback = callback;
	}

	@JavascriptInterface
	public void onOpenIDLoginSuccess(final String guid, final String first, final String last, final String email, final String lang) {
		userService.onOpenIDLoginSuccess(guid, first, last, email, lang, callback, handler, context);
	}

	@JavascriptInterface
	public void onOpenIDLoginError(final String errorMessage) {
		userService.onOpenIDLoginError(errorMessage, callback, handler, context);
	}

}

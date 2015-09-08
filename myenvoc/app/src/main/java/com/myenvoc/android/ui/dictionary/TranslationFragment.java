package com.myenvoc.android.ui.dictionary;

import java.util.Collection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.TranslationMeaning;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.ui.SettingsActivity;
import com.myenvoc.android.ui.user.SignInActivity;
import com.myenvoc.android.ui.user.SignInActivity.SignInType;
import com.myenvoc.commons.ThreadPoolExecutor;

public class TranslationFragment extends DictionaryTab {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		setupActivity();
		final String word = getWordFromIntent();

		final View tab = inflater.inflate(R.layout.dictionary_tab, null);

		installWordTitle(tab, getActivitySafe());

		final View inProgress = onStartQuerying(tab, word);
		setTranscription(tab);
		dictionaryService.queryTranslation(word, new GenericCallback<Collection<TranslationMeaning>>() {

			@Override
			public void onSuccess(final Collection<TranslationMeaning> data) {
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				renderTranslation(tab, data, word);
			}

			@Override
			public void onError(final Failure failure) {
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				if (failure == null) {
					handleError(tab, failure, TranslationFragment.this.getActivitySafe());
				} else {
					switch (failure.getValue()) {
					case ANONYMOUS_USER_ACCESS_TRANSLATION:
						handleAnonnymousAccessTranslation(getActivitySafe());
						break;
					case NATIVE_LANG_NOT_SET:
						handleNativeLangNotSet(getActivitySafe());
						break;
					default:
						handleError(tab, failure, TranslationFragment.this.getActivitySafe());
					}
				}

			}

		}, ThreadPoolExecutor.getCallbackInUIThreadParameters(MyenvocHomeActivity.getHandler()));
		return tab;
	}

	public static void handleNativeLangNotSet(final FragmentActivity activity) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle(R.string.translationLangNotSet);

		alertDialogBuilder.setMessage(R.string.specifyTranslationLang).setCancelable(true)
				.setPositiveButton(R.string.userProfile, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						Intent settingsIntent = new Intent(activity, SettingsActivity.class);
						activity.startActivity(settingsIntent);
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});

		alertDialogBuilder.create().show();
	}

	public static void handleAnonnymousAccessTranslation(final FragmentActivity activity) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle(R.string.translationLangNotSet);

		alertDialogBuilder.setMessage(R.string.anonymAccessTranslation).setCancelable(true)
				.setPositiveButton(R.string.signIn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						Intent intent = new Intent(activity, SignInActivity.class);
						intent.putExtra(SignInType.TYPE, SignInType.SIGN_IN.name());
						activity.finish();
						activity.startActivity(intent);

					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});

		alertDialogBuilder.create().show();
	}

	private void renderTranslation(final View tab, final Collection<TranslationMeaning> data, final String word) {
		ViewGroup parent = (ViewGroup) tab.findViewById(R.id.tabContent);

		if (data.isEmpty()) {
			parent.addView(noDataFound(R.string.no_translation_found, word));
		} else {
			parent.addView(TranslationWidget.create(word, data, getActivitySafe(), null));
		}
	}

}

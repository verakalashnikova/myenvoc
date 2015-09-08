package com.myenvoc.android.ui.dictionary;

import java.util.Collection;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.domain.TranslationMeaning;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.vocabulary.VocabularyService.MyWordUpdateAction;
import com.myenvoc.commons.ThreadPoolExecutor;
import com.myenvoc.commons.VocabularyUtils;

public class PickTranslationFragment extends PickInfoDictionaryTab {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		setupActivity();
		final String word = getLemma();

		final View tab = inflater.inflate(R.layout.pick_info_dictionary_tab, null);

		final View inProgress = onStartQuerying(tab, word);

		final FragmentActivity activity = getActivitySafe();
		dictionaryService.queryTranslation(word, new GenericCallback<Collection<TranslationMeaning>>() {

			@Override
			public void onSuccess(final Collection<TranslationMeaning> data) {
				// no need to do anything - activity is dead
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				renderTranslation(tab, data, word, activity);
			}

			@Override
			public void onError(final Failure failure) {
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				if (failure == null) {
					DictionaryTab.handleError(tab, failure, activity);
				} else {
					switch (failure.getValue()) {
					case ANONYMOUS_USER_ACCESS_TRANSLATION:
						TranslationFragment.handleAnonnymousAccessTranslation(activity);
						break;
					case NATIVE_LANG_NOT_SET:
						TranslationFragment.handleNativeLangNotSet(activity);
						break;
					default:
						DictionaryTab.handleError(tab, failure, activity);
					}
				}

			}
		}, ThreadPoolExecutor.getCallbackInUIThreadParameters(MyenvocHomeActivity.getHandler()));
		return tab;
	}

	private void renderTranslation(final View tab, final Collection<TranslationMeaning> data, final String word,
			final FragmentActivity activity) {

		ViewGroup parent = (ViewGroup) tab.findViewById(R.id.tabContent);

		if (data.isEmpty()) {
			parent.addView(noDataFound(R.string.no_translation_found, word));
		} else {
			parent.addView(TranslationWidget.create(word, data, activity, new TranslationWidget.TranslationSelectListener() {

				@Override
				public void onSelect(final String translation) {
					MyWord myWord = getMyWord();

					if (VocabularyUtils.hasTranslation(myWord, translation)) {
						Toast.makeText(activity, R.string.translationAlreadyAssigned, Toast.LENGTH_LONG).show();
						return;
					}

					updateMyWord(new MyWordUpdateAction() {

						@Override
						public void run() {
							VocabularyUtils.addTranslation(getFreshWord(), translation);
						}

					});

					backToEditMyWord();
				}
			}));
		}
	}
}

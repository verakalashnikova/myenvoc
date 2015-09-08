package com.myenvoc.android.ui.dictionary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.WordNetDefinition;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.ThreadPoolExecutor;

/**
 * Displays a word and its definition.
 */
public class WordnetDefinitionFragment extends DictionaryTab {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		setupActivity();
		final String word = getWordFromIntent();

		final View tab = inflater.inflate(R.layout.dictionary_tab, null);
		installWordTitle(tab, getActivitySafe());
		final View inProgress = onStartQuerying(tab, word);
		dictionaryService.queryDefinition(word, new GenericCallback<WordNetDefinition>() {

			@Override
			public void onSuccess(final WordNetDefinition data) {
				if (isActivityDead()) {
					return;
				}
				handleDefinitionResults(tab, data);
				hideProgress(inProgress);
				renderWordDefinition(tab, data, word);
			}

			@Override
			public void onError(final Failure failure) {
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				handleError(tab, failure, WordnetDefinitionFragment.this.getActivitySafe());
			}

		}, ThreadPoolExecutor.getCallbackInUIThreadParameters(MyenvocHomeActivity.getHandler()));
		return tab;
	}

	public void renderWordDefinition(final View tab, final WordNetDefinition data, final String word) {

		ViewGroup parent = (ViewGroup) tab.findViewById(R.id.tabContent);

		if (data == null) {
			parent.addView(noDataFound(R.string.no_definition_found, word));
		} else {
			parent.addView(WordNetSynsetWidget.createWordNetDefinitionWidget(getActivitySafe(), data.getS(), null));
		}

	}

	private void handleDefinitionResults(final View tab, final WordNetDefinition data) {
		if (data == null) {
			return;
		}
		if (!getWordFromIntent().equals(data.getL())) {
			/** word is different due to inflection rules on server. **/
			getActivitySafe().getIntent().putExtra(CommonUtils.WORD, data.getL());

			/**
			 * again call the following to adjust add/edit button label
			 **/
			handleMyWordExistence(tab, data.getL());
			setWordTitle(tab, data.getL());
		}

		if (data.getT() != null) {
			getActivitySafe().getIntent().putExtra(CommonUtils.TRANSCRIPTION, data.getT());
			setTranscription(tab);
		}
	}
}

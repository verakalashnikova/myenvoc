package com.myenvoc.android.ui.dictionary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.domain.MyWordContext;
import com.myenvoc.android.domain.WordNetDefinition;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.vocabulary.VocabularyService.MyWordUpdateAction;
import com.myenvoc.android.ui.dictionary.WordNetSynsetWidget.DefinitionSelectListener;
import com.myenvoc.commons.ThreadPoolExecutor;
import com.myenvoc.commons.VocabularyUtils;

/**
 * Displays a word and its definition.
 */
public class PickWordnetDefinitionFragment extends PickInfoDictionaryTab {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		setupActivity();

		final String word = getLemma();
		final View tab = inflater.inflate(R.layout.pick_info_dictionary_tab, null);
		final View inProgress = onStartQuerying(tab, word);
		dictionaryService.queryDefinition(word, new GenericCallback<WordNetDefinition>() {

			@Override
			public void onSuccess(final WordNetDefinition data) {
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				renderWordDefinition(tab, data, word);

			}

			@Override
			public void onError(final Failure failure) {
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				DictionaryTab.handleError(tab, failure, PickWordnetDefinitionFragment.this.getActivitySafe());
			}

		}, ThreadPoolExecutor.getCallbackInUIThreadParameters(MyenvocHomeActivity.getHandler()));
		return tab;
	}

	public void renderWordDefinition(final View tab, final WordNetDefinition data, final String word) {

		ViewGroup parent = (ViewGroup) tab.findViewById(R.id.tabContent);

		if (data == null) {
			parent.addView(noDataFound(R.string.no_definition_found, word));
		} else {
			parent.addView(WordNetSynsetWidget.createWordNetDefinitionWidget(getActivitySafe(), data.getS(),
					new DefinitionSelectListener() {

						@Override
						public void onSelect(final MyWordContext context) {
							MyWord myWord = getMyWord();

							for (MyWordContext myWordContext : myWord.getContexts()) {
								if (myWordContext.equals(context)) {
									Toast.makeText(PickWordnetDefinitionFragment.this.getActivitySafe(),
											R.string.definitionAlreadyAssigned, Toast.LENGTH_LONG).show();
									return;
								}
							}
							updateMyWord(new MyWordUpdateAction() {

								@Override
								public void run() {
									VocabularyUtils.addContext(getFreshWord(), context);
								}

							});
							backToEditMyWord();
						}
					}));
		}

	}
}

package com.myenvoc.android.ui.dictionary;

import javax.inject.Inject;

import android.view.View;
import android.widget.TextView;

import com.myenvoc.R;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.service.dictionary.DictionaryService;
import com.myenvoc.android.service.dictionary.ImageService;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.android.service.vocabulary.VocabularyService.MyWordUpdateAction;
import com.myenvoc.android.ui.MyenvocFragment;
import com.myenvoc.commons.CommonUtils;

public abstract class PickInfoDictionaryTab extends MyenvocFragment {

	protected static final String TAG = PickInfoDictionaryTab.class.getName();
	@Inject
	DictionaryService dictionaryService;

	@Inject
	VocabularyService vocabularyService;

	@Inject
	ImageService imageService;

	protected String getLemma() {
		return getActivitySafe().getIntent().getExtras().getString(CommonUtils.MY_WORD_LEMMA);
	}

	protected MyWord getMyWord() {
		int myWordId = getMyWordId();
		return vocabularyService.findMyWordById(myWordId);
	}

	protected int getMyWordId() {
		return getActivitySafe().getIntent().getExtras().getInt(CommonUtils.MY_WORD_ID);
	}

	protected void updateMyWord(final MyWordUpdateAction updateAction) {
		vocabularyService.updateMyWord(updateAction, getMyWordId());
	}

	protected void backToEditMyWord() {
		// Intent editMyWordIntent = new
		// Intent(getActivitySafe(),
		// EditVocabularyActivity.class);
		// editMyWordIntent.putExtra(CommonUtils.MY_WORD_ID, getMyWordId());
		//
		// EditVocabularyActivity.copyVocabularyReturnAddressIfNeeded(getActivitySafe().getIntent(),
		// editMyWordIntent);
		//
		// startActivity(editMyWordIntent);
		getActivitySafe().finish();
	}

	protected View onStartQuerying(final View tab, final String word) {
		TextView wordTitle = (TextView) tab.findViewById(R.id.wordTitle);
		wordTitle.setText(word);

		return showProgress(tab);

	}

	protected View noDataFound(final int resourceId, final String word) {
		TextView noDataFound = new TextView(getActivitySafe());
		noDataFound.setText(getActivitySafe().getResources().getString(resourceId, word));
		return noDataFound;
	}

	protected View showProgress(final View tab) {
		final View inProgress = tab.findViewById(R.id.inProgress);
		inProgress.setVisibility(View.VISIBLE);
		return inProgress;
	}

	protected void hideProgress(final View inProgress) {
		inProgress.setVisibility(View.GONE);
	}
}

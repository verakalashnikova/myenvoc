package com.myenvoc.android.ui.dictionary;

import javax.inject.Inject;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.FailureValue;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.service.dictionary.DictionaryService;
import com.myenvoc.android.service.dictionary.ImageService;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.android.ui.MyenvocFragment;
import com.myenvoc.android.ui.vocabulary.ViewMyWordWidget;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.StringUtils;
import com.myenvoc.commons.VocabularyUtils;


public abstract class DictionaryTab extends MyenvocFragment {

	protected static final String TAG = DictionaryTab.class.getName();
	@Inject
	DictionaryService dictionaryService;

	@Inject
	VocabularyService vocabularyService;

	@Inject
	ImageService imageService;

	@Inject
	UserService userService;

	protected TextView wordTitle;

	private View pronunciationInProgress;

	protected void installWordTitle(final View tab, final Context context) {
		wordTitle = (TextView) tab.findViewById(R.id.wordTitle);
		pronunciationInProgress = tab.findViewById(R.id.pronunciationInProgress);
		ViewMyWordWidget.initializeLemmaTextView(context, wordTitle);
	}

	protected String getWordFromIntent() {
		return getMyenvocDictionaryActivity().getWordFromIntent();
	}

	private String getTranscriptionFromIntent() {
		return getMyenvocDictionaryActivity().getTranscriptionFromIntent();
	}

	protected View onStartQuerying(final View tab, final String word) {
		setWordTitle(tab, word);
		wordTitle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				String word = getWordFromIntent();
				VocabularyUtils.playTranscription(word, getActivitySafe(), pronunciationInProgress);
			}

		});

		Button addOrEditWord = (Button) tab.findViewById(R.id.addWordButton);
		addOrEditWord.setVisibility(View.INVISIBLE);
		handleMyWordExistence(tab, word);
		return showProgress(tab);

	}

	protected void setWordTitle(final View tab, final String word) {
		wordTitle.setText(word);
	}

	protected void setTranscription(final View tab) {
		String transcription = getTranscriptionFromIntent();
		if (StringUtils.isEmpty(transcription)) {
			return;
		}

		VocabularyUtils.appendTranscription(transcription, wordTitle, getActivitySafe());
	}

	protected void handleMyWordExistence(final View parent, final String word) {

		Button addOrEditWord = setupUIOnlyButtonName(parent, word);

		addOrEditWord.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				/**
				 * pass getWordFromIntent() (not 'word' parameter) as it might
				 * be changed from 'tries' to 'tree'.
				 */
				getMyenvocDictionaryActivity().handleOnAddEditClick();
			}

		});
	}

	private Button setupUIOnlyButtonName(final View parent, final String word) {
		MyWord myWord = vocabularyService.getMyWord(word);

		Button addOrEditWord = (Button) parent.findViewById(R.id.addWordButton);
		addOrEditWord.setVisibility(View.VISIBLE);

		getActivitySafe().getIntent().putExtra(CommonUtils.IS_NEW, myWord == null);
		getMyenvocDictionaryActivity().refreshAddEditIcon();

		final String label = getPart(word);
		if (myWord != null) {
			addOrEditWord.setText(getActivitySafe().getString(R.string.edit_my_word, label));
		} else {
			addOrEditWord.setText(getActivitySafe().getString(R.string.add_to_vocabulary, label));
		}
		return addOrEditWord;
	}

	private MyenvocDictionaryActivity getMyenvocDictionaryActivity() {
		return (MyenvocDictionaryActivity) getActivitySafe();
	}

	private String getPart(final String word) {
		final String label;
		if (word.length() <= 4) {
			label = word;
		} else {
			label = word.substring(0, 4) + "... ";
		}
		return label;
	}

	protected void handleAddFromImages(final String imageUrl) {
		getMyenvocDictionaryActivity().handleOnAddEditClick(getWordFromIntent(), getTranscriptionFromIntent(), imageUrl);
	}

	protected View noDataFound(final int resourceId, final String word) {
		TextView noDataFound = new TextView(getActivitySafe());
		noDataFound.setText(getActivitySafe().getResources().getString(resourceId, word));
		return noDataFound;
	}

	public static void handleError(final View tab, final Failure failure, final Context context) {
		if (failure == null) {
			Toast.makeText(context, FailureValue.GENERAL_ERROR.getLocalized(context), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, failure.getLocalized(context), Toast.LENGTH_LONG).show();
		}
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

package com.myenvoc.android.service.vocabulary;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.GoogleImageSearchResponseResult;
import com.myenvoc.android.domain.GoolgeImageSearchResult;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.domain.MyWordContext;
import com.myenvoc.android.domain.TranslationMeaning;
import com.myenvoc.android.domain.WordNetDefinition;
import com.myenvoc.android.domain.WordNetSynset;
import com.myenvoc.android.service.dictionary.DictionaryService;
import com.myenvoc.android.service.dictionary.ImageService;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.service.vocabulary.VocabularyService.MyWordUpdateAction;
import com.myenvoc.android.ui.vocabulary.EditVocabularyActivity;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.SerialMyTaskExecutor;
import com.myenvoc.commons.StringUtils;
import com.myenvoc.commons.ThreadPoolExecutor;
import com.myenvoc.commons.VocabularyUtils;

public class MyWordInitialDataLoadService {
	protected static final String TAG = MyWordInitialDataLoadService.class.getName();

	@Inject
	VocabularyService vocabularyService;

	@Inject
	DictionaryService dictionaryService;

	@Inject
	ImageService imageService;

	@Inject
	UserService userService;

	public void populateMyWord(final String lemma, final MyWord myWord, final EditVocabularyActivity editVocabularyActivity,
			final Handler handler) {

		int myWordId = myWord.getAttributes().getId();

		if (StringUtils.isEmpty(myWord.getTranslation())) {
			addFirstTransation(myWordId, editVocabularyActivity, handler, lemma);
		}
		if (myWord.getContexts().isEmpty()) {
			addFirstDefinition(myWordId, editVocabularyActivity, handler, lemma);
		}
		if (StringUtils.isEmpty(myWord.getImageUrl())) {
			addFirstImage(myWordId, editVocabularyActivity, handler, lemma);
		}
	}

	private void addFirstImage(final int myWordId, final EditVocabularyActivity editVocabularyActivity, final Handler handler,
			final String lemma) {
		dictionaryService.queryGoogleImages(lemma, new GenericCallback<GoolgeImageSearchResult>() {

			@Override
			public void onSuccess(final GoolgeImageSearchResult data) {

				List<GoogleImageSearchResponseResult> googleImages = VocabularyUtils.getGoogleImagesFromResponse(data);
				if (googleImages.isEmpty()) {
					return;
				}

				final String url = googleImages.iterator().next().getTbUrl();

				vocabularyService.updateMyWord(new MyWordUpdateAction() {

					@Override
					public void run() {
						getFreshWord().setImageUrl(url);
					}
				}, myWordId);

				imageService.load(url, new GenericCallback<Bitmap>() {

					@Override
					public void onSuccess(final Bitmap data) {

						handler.post(new Runnable() {

							@Override
							public void run() {
								editVocabularyActivity.setImageBitmap(data);
							}
						});
					}

					@Override
					public void onError(final Failure failure) {
						Log.e(TAG, "Unable to get actual image for: " + lemma);
					}
				}, SerialMyTaskExecutor.SERIAL_EXECUTOR_PARAMETERS);

			}

			@Override
			public void onError(final Failure failure) {
				Log.e(TAG, "Unable to get google image search result for: " + lemma);
			}
		}, ThreadPoolExecutor.TP_EXECUTOR_DEFAULT_PARAMETERS);
	}

	private void addFirstDefinition(final int myWordId, final EditVocabularyActivity editVocabularyActivity, final Handler handler,
			final String lemma) {
		dictionaryService.queryDefinition(lemma, new GenericCallback<WordNetDefinition>() {

			@Override
			public void onSuccess(final WordNetDefinition definition) {
				if (definition == null) {
					return;
				}
				List<WordNetSynset> synsets = definition.getS();
				if (CommonUtils.isEmpty(synsets)) {
					return;
				}
				WordNetSynset synset = synsets.iterator().next();
				final MyWordContext context = VocabularyUtils.convert(synset);

				vocabularyService.updateMyWord(new MyWordUpdateAction() {

					@Override
					public void run() {
						VocabularyUtils.addContext(getFreshWord(), context);
					}
				}, myWordId);

				handler.post(new Runnable() {

					@Override
					public void run() {
						editVocabularyActivity.addDefinition(context);
					}
				});
			}

			@Override
			public void onError(final Failure failure) {
				Log.e(TAG, "Unable to get definition in for: " + lemma);
			}
		}, ThreadPoolExecutor.TP_EXECUTOR_DEFAULT_PARAMETERS);
	}

	private void addFirstTransation(final int myWordId, final EditVocabularyActivity editVocabularyActivity, final Handler handler,
			final String lemma) {
		dictionaryService.queryTranslation(lemma, new GenericCallback<Collection<TranslationMeaning>>() {

			/** in bk thread, on thread-pool. */
			@Override
			public void onSuccess(final Collection<TranslationMeaning> collection) {
				if (CommonUtils.isEmpty(collection)) {
					return;
				}
				TranslationMeaning translationMeaning = collection.iterator().next();
				final String translation = VocabularyUtils.getFlatTranslation(translationMeaning);

				vocabularyService.updateMyWord(new MyWordUpdateAction() {

					@Override
					public void run() {
						VocabularyUtils.addTranslation(getFreshWord(), translation);
					}
				}, myWordId);

				handler.post(new Runnable() {

					@Override
					public void run() {
						editVocabularyActivity.addTranslation(translation);
					}
				});

			}

			@Override
			public void onError(final Failure failure) {
				Log.e(TAG, "Unable to get translation in for: " + lemma);
			}
		}, ThreadPoolExecutor.TP_EXECUTOR_DEFAULT_PARAMETERS);
	}
}

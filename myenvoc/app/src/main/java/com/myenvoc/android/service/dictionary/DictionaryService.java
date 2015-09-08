package com.myenvoc.android.service.dictionary;

import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;

import com.myenvoc.android.dao.dictionary.VocabularyDatabase;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.FailureValue;
import com.myenvoc.android.domain.GoolgeImageSearchResult;
import com.myenvoc.android.domain.Languages;
import com.myenvoc.android.domain.TranslationMeaning;
import com.myenvoc.android.domain.UserProfile;
import com.myenvoc.android.domain.WordNetDefinition;
import com.myenvoc.android.inject.BaseResource;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.network.NetworkService;
import com.myenvoc.android.service.network.Resource;
import com.myenvoc.android.service.network.ServerRequest;
import com.myenvoc.android.service.user.User;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.MyTaskExecutor.ExecutorParameters;
import com.myenvoc.commons.MyenvocException;
import com.myenvoc.commons.StringUtils;

public class DictionaryService {
	private static final String TAG = DictionaryService.class.getName();

	private static final String WORD_NET_DEFINITION_KEY = "WNDN";

	private static final String TRAN_KEY = "TRN";

	private static final String IMAGE_KEY = "IMG";

	private final Resource wordNetResource;

	private final Resource translationResource;

	private final Resource googleImageSearch = Resource.forUrl("https://ajax.googleapis.com/ajax/services/search/images?v=1.0");

	@Inject
	NetworkService networkService;

	@Inject
	VocabularyDatabase vocabularyDatabase;

	@Inject
	VocabularyService vocabularyService;

	@Inject
	UserService userService;

	@Inject
	public DictionaryService(@BaseResource final Resource baseResource) {
		wordNetResource = baseResource.path("word/getWordNetL");
		translationResource = baseResource.path("pub/dict/tran");
	}

	private class DefinitionLoader extends AbstractLoader<WordNetDefinition> {

		@Override
		protected String getCacheKey(final String request) {
			return WORD_NET_DEFINITION_KEY + request;
		}

		@Override
		protected WordNetDefinition loadFromDataBase(final String request) {
			return vocabularyDatabase.findDefinition(request);
		}

		@Override
		protected void saveToDatabase(final String request, final WordNetDefinition result) {
			vocabularyDatabase.saveDefinition(request, result);
		}

		@Override
		protected WordNetDefinition loadFromInternet(final String request) {
			ServerRequest serverRequest = wordNetResource.requestBuilderInitParams().queryParameter("lemma", request)
					.buildFor(WordNetDefinition.class);

			return networkService.synchronousRequest(serverRequest);
		}
	}

	private class TranslationLoader extends AbstractLoader<Collection<TranslationMeaning>> {

		public void loadTranslation(final String word, final String lang, final GenericCallback<Collection<TranslationMeaning>> callback,
				final ExecutorParameters executorParameters) {
			String encoded = word + lang + String.valueOf(lang.length());
			load(encoded, callback, executorParameters);
		}

		@Override
		protected String getCacheKey(final String request) {
			return TRAN_KEY + request;
		}

		@Override
		protected Collection<TranslationMeaning> loadFromDataBase(final String request) {
			return vocabularyDatabase.findTranslation(request);
		}

		@Override
		protected void saveToDatabase(final String request, final Collection<TranslationMeaning> result) {
			vocabularyDatabase.saveTranslation(request, result);
		}

		@Override
		protected Collection<TranslationMeaning> loadFromInternet(final String request) {
			int langLength = Integer.parseInt(request.substring(request.length() - 1));
			String word = request.substring(0, request.length() - langLength - 1);
			String lang = request.substring(request.length() - 2 - 1, request.length() - 1);

			ServerRequest serverRequest = translationResource.requestBuilderInitParams().queryParameter("lemma", word)
					.queryParameter("lang", lang).buildFor(TranslationMeaning.class, true);

			return networkService.synchronousRequest(serverRequest);
		}
	}

	private class ImageLoader extends AbstractLoader<GoolgeImageSearchResult> {

		@Override
		protected String getCacheKey(final String request) {
			return IMAGE_KEY + request;
		}

		@Override
		protected GoolgeImageSearchResult loadFromDataBase(final String request) {
			return vocabularyDatabase.findImageSearch(request);
		}

		@Override
		protected void saveToDatabase(final String request, final GoolgeImageSearchResult result) {
			vocabularyDatabase.saveImageSearch(request, result);
		}

		@Override
		protected GoolgeImageSearchResult loadFromInternet(final String request) {
			ServerRequest serverRequest = googleImageSearch.requestBuilder().queryParameter("q", request)
					.buildFor(GoolgeImageSearchResult.class, false);
			return networkService.synchronousRequest(serverRequest);
		}
	}

	private final DefinitionLoader definitionLoader = new DefinitionLoader();

	private final TranslationLoader translationLoader = new TranslationLoader();

	private final ImageLoader imageLoader = new ImageLoader();

	public void queryDefinition(final String word, final GenericCallback<WordNetDefinition> callback,
			final ExecutorParameters executorParameters) {
		definitionLoader.load(word, callback, executorParameters);
	}

	/**
	 * @throws MyenvocException
	 *             is user is not registered, or if registered user has not
	 *             specified native language
	 */
	public void queryTranslation(final String word, final GenericCallback<Collection<TranslationMeaning>> callback,
			final ExecutorParameters executorParameters) {

		User user = userService.getUser();
		String translationLanguage = null;
		if (!user.isAnonymous()) {
			translationLanguage = userService.getUserProfile().getNativeLanguage();
		}

		if (StringUtils.isEmpty(translationLanguage)) {
			translationLanguage = detectLang();
		}

		if (StringUtils.isEmpty(translationLanguage)) {
			if (user.isAnonymous()) {
				callback.onError(Failure.byValue(FailureValue.ANONYMOUS_USER_ACCESS_TRANSLATION));
				return;
			}
			UserProfile userProfile = userService.getUserProfile();
			if (CommonUtils.isEmpty(userProfile.getNativeLanguage())) {
				callback.onError(Failure.byValue(FailureValue.NATIVE_LANG_NOT_SET));
				return;
			}
		}

		translationLoader.loadTranslation(word, translationLanguage, callback, executorParameters);
	}

	private String detectLang() {
		if (Locale.getDefault() != null) {
			String defaultLang = Locale.getDefault().getLanguage();
			Languages resolved = Languages.resolve(defaultLang);
			if (resolved != null && resolved != Languages.English) {
				return resolved.getLang();
			}

		}
		return null;
	}

	public void queryGoogleImages(final String word, final GenericCallback<GoolgeImageSearchResult> callback,
			final ExecutorParameters executorParameters) {
		imageLoader.load(word, callback, executorParameters);
	}

}

package com.myenvoc.android.service.vocabulary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import android.database.Cursor;
import android.util.Log;

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.myenvoc.android.dao.dictionary.VocabularyDatabase;
import com.myenvoc.android.dao.dictionary.VocabularyOrder;
import com.myenvoc.android.domain.AnonymousUser;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.JSONConverter;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.domain.MyWordAttributes;
import com.myenvoc.android.domain.MyWordSync;
import com.myenvoc.android.domain.MyWordSyncResponse;
import com.myenvoc.android.domain.MyWordSyncResult;
import com.myenvoc.android.domain.UserProfile;
import com.myenvoc.android.inject.BaseResource;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.network.NetworkService;
import com.myenvoc.android.service.network.Resource;
import com.myenvoc.android.service.network.ServerRequest;
import com.myenvoc.android.service.network.ServerRequest.RequestType;
import com.myenvoc.android.service.user.User;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.StringUtils;

public class VocabularyService {
	private static final String TAG = VocabularyService.class.getName();

	public interface VocabularySyncListener {
		void onVocabularySyncSuccess();

		void onError(Failure failure);
	}

	@Inject
	public VocabularyDatabase vocabularyDatabase;

	@Inject
	UserService userService;

	@Inject
	NetworkService networkService;

	private final Resource addWordResource;
	private final Resource updateWordResource;
	private final Resource removeWordResource;
	private final Resource syncWordsResource;

	private static final Object MY_WORD_UPDATE_MUTEX = new Object();

	private static final long REMOTE_UPDATE_DELAY = 60;

	private volatile ScheduledExecutorService asyncWordUpdateScheduler;
	private final ConcurrentMap<Integer, ScheduledFuture<?>> pendingUpdate = Maps.newConcurrentMap();

	@Inject
	public VocabularyService(@BaseResource final Resource baseResource) {
		addWordResource = baseResource.path("sync/addWord", true);
		updateWordResource = baseResource.path("sync/updateWord", true);
		removeWordResource = baseResource.path("sync/removeWord", true);
		syncWordsResource = baseResource.path("sync/syncWords", true);
	}

	public static abstract class MyWordUpdateAction implements Runnable {
		private MyWord freshWord;

		public void setFreshWord(final MyWord myWord) {
			this.freshWord = myWord;
		}

		public MyWord getFreshWord() {
			return freshWord;
		}

	}

	public MyWord updateMyWord(final MyWordUpdateAction updateAction, final int myWordId) {
		// FIXME: actually, it's easy to make this async
		synchronized (MY_WORD_UPDATE_MUTEX) {
			MyWord myWordFresh = findMyWordById(myWordId);
			updateAction.setFreshWord(myWordFresh);
			updateAction.run();
			addOrUpdateMyWord(myWordFresh);
			return myWordFresh;
		}
	}

	/***
	 * Adds word locally, then schedules remotely add/update.
	 */
	public void addOrUpdateMyWord(final MyWord myWord) {
		if (myWord.getAttributes().isNew()) {
			vocabularyDatabase.addMyWord(myWord, -1, -1, true, getUser());
		} else {
			vocabularyDatabase.updateMyWord(myWord.getAttributes().getId(), myWord, -1, -1, true);
		}
		scheduleUpdate(myWord.getAttributes().getId());
	}

	private void scheduleUpdate(final int myWordId) {
		if (getUser().isAnonymous()) {
			return;
		}
		handleScheduler(myWordId);
		ScheduledFuture<?> future = asyncWordUpdateScheduler.schedule(new Runnable() {

			@Override
			public void run() {
				MyWord myWord = findMyWordById(myWordId);

				MyWordSync parameter = new MyWordSync(myWord.getAttributes().getServerId(), myWord.getAttributes().getServerVersion(),
						myWord.getAttributes().getId(), true, false, myWord);

				if (parameter.getServerId() > 0) {
					requestServerSyncSynchronously(
							updateWordResource.requestBuilder(RequestType.POST).withPOSTParameter(JSONConverter.convertToString(parameter))
									.withAuthToken(getAuthToken()).buildFor(MyWordSyncResponse.class), null);
				} else {
					requestServerSyncSynchronously(
							addWordResource.requestBuilder(RequestType.POST).withPOSTParameter(JSONConverter.convertToString(parameter))
									.withAuthToken(getAuthToken()).buildFor(MyWordSyncResponse.class), null);
				}
				pendingUpdate.remove(myWordId);
			}
		}, REMOTE_UPDATE_DELAY, TimeUnit.SECONDS);
		pendingUpdate.put(myWordId, future);
	}

	private void scheduleDelete(final int myWordId) {
		if (getUser().isAnonymous()) {
			return;
		}
		handleScheduler(myWordId);
		ScheduledFuture<?> future = asyncWordUpdateScheduler.schedule(new Runnable() {

			@Override
			public void run() {
				MyWord myWord = findMyWordById(myWordId);

				if (myWord.getAttributes().getServerId() > 0) {
					MyWordSync parameter = new MyWordSync(myWord.getAttributes().getServerId(), myWord.getAttributes().getServerVersion(),
							myWord.getAttributes().getId(), false, true, null);
					requestServerSyncSynchronously(
							removeWordResource.requestBuilder(RequestType.POST).withPOSTParameter(JSONConverter.convertToString(parameter))
									.withAuthToken(getAuthToken()).buildFor(MyWordSyncResponse.class), null);
				}
				pendingUpdate.remove(myWordId);
			}
		}, REMOTE_UPDATE_DELAY, TimeUnit.SECONDS);
		pendingUpdate.put(myWordId, future);
	}

	private void handleScheduler(final int myWordId) {
		if (asyncWordUpdateScheduler == null) {
			asyncWordUpdateScheduler = Executors.newScheduledThreadPool(1);
		}

		ScheduledFuture<?> scheduledFuture = pendingUpdate.get(myWordId);
		if (scheduledFuture != null && !scheduledFuture.isDone()) {
			scheduledFuture.cancel(false);
		}
	}

	public void deleteMyWord(final MyWord myWord) {
		vocabularyDatabase.removeMyWord(myWord.getAttributes(), getUser());
		scheduleDelete(myWord.getAttributes().getId());
	}

	public void deleteMyWord(final int myWordId) {
		deleteMyWord(findMyWordById(myWordId));
	}

	public List<MyWord> findAllMyWords(final VocabularyOrder vocabularyOrder) {
		Stopwatch stopwatch = new Stopwatch().start();
		Collection<MyWord> allMyWords = vocabularyDatabase.findAllMyWords(getUser(), vocabularyOrder);

		List<MyWord> result = Lists.newArrayList(allMyWords);
		result = shuffleIfNeeded(vocabularyOrder, result);
		Log.i(TAG, "Load all my words took " + stopwatch.stop());
		return result;
	}

	public Vocabulary findVocabulary(final VocabularyFilter vocabularyFilter, final VocabularyOrder vocabularyOrder) {
		if (vocabularyFilter == null) {
			return new Vocabulary(findAllMyWords(vocabularyOrder));
		}
		Collection<MyWord> allMyWords = vocabularyDatabase.findAllMyWords(getUser(), vocabularyOrder);
		final Date after = vocabularyFilter.getAddedAt().getFilterDate();
		final String startsWith = StringUtils.isEmpty(vocabularyFilter.getStartsWith()) ? null : vocabularyFilter.getStartsWith()
				.toLowerCase(Locale.US);
		Collection<MyWord> filtered = Collections2.filter(allMyWords, new Predicate<MyWord>() {

			@Override
			public boolean apply(final MyWord w) {
				if (startsWith != null) {
					// FIXME: only startsWith.length may be lowercased
					if (!w.getLemma().toLowerCase(Locale.US).startsWith(startsWith)) {
						return false;
					}
				}
				if (after != null) {
					if (w.getAdded() != null && w.getAdded().before(after)) {
						return false;
					}
				}
				return vocabularyFilter.getWordStatuses().contains(w.getStatus());
			}
		});
		List<MyWord> result = Lists.newArrayList(filtered);
		result = shuffleIfNeeded(vocabularyOrder, result);

		return new Vocabulary(result, allMyWords.size());
	}

	private List<MyWord> shuffleIfNeeded(final VocabularyOrder vocabularyOrder, final List<MyWord> allMyWords) {
		if (VocabularyOrder.RANDOM == vocabularyOrder) {
			Collections.shuffle(allMyWords);
		}
		return allMyWords;
	}

	public MyWord findMyWordById(final int myWordId) {
		return vocabularyDatabase.findMyWordById(myWordId);
	}

	/**
	 * Called at app start, or by clicking Refresh button.
	 */
	public void syncRemote(final UserProfile user, final VocabularySyncListener syncListener) {
		Cursor myWordsCursor = vocabularyDatabase.findAllMyWordsRaw(user, null);
		Cursor myWordsRemoved = vocabularyDatabase.findAllRemovedMyWordsRaw(user);
		if (myWordsCursor == null || myWordsRemoved == null) {
			Log.e(TAG, "Error trying to sync vocabulary");
			return;
		}
		List<MyWordSync> syncs = new ArrayList<MyWordSync>();
		while (myWordsCursor.moveToNext()) {
			int clientId = myWordsCursor.getInt(0);
			int serverId = myWordsCursor.getInt(1);
			int serverVersion = myWordsCursor.getInt(2);
			int updatedLocally = myWordsCursor.getInt(3);

			if (serverId <= 0 || updatedLocally == 1) {
				/**
				 * newly create word on client only OR word was updated locally.
				 */
				syncs.add(new MyWordSync(serverId, serverVersion, clientId, true, false, JSONConverter.loadSingleObject(
						myWordsCursor.getString(4), MyWord.class)));
			} else {
				syncs.add(new MyWordSync(serverId, serverVersion, clientId, false, false, null));
			}
		}
		myWordsCursor.close();

		while (myWordsRemoved.moveToNext()) {
			syncs.add(new MyWordSync(myWordsRemoved.getInt(1), -1, myWordsRemoved.getInt(0), false, true, null));
		}
		myWordsRemoved.close();

		requestServerSyncAsynchronously(
				syncWordsResource.requestBuilder(RequestType.POST).withAuthToken(getAuthToken())
						.withPOSTParameter(JSONConverter.convertArrayToString(syncs.toArray(new MyWordSync[syncs.size()])))
						.buildFor(MyWordSyncResponse.class), syncListener);
	}

	protected void requestServerSyncAsynchronously(final ServerRequest request, final VocabularySyncListener syncListener) {
		networkService.asynchronousPOSTRequest(request, new GenericCallback<MyWordSyncResponse>() {

			@Override
			public void onSuccess(final MyWordSyncResponse result) {
				handleSyncResponse(result, syncListener);
			}

			@Override
			public void onError(final Failure failure) {
				if (syncListener != null) {
					syncListener.onError(failure);
				}
				Log.e(TAG, "Unable to add word remotely " + failure);
			}

		});
	}

	protected void requestServerSyncSynchronously(final ServerRequest request, final VocabularySyncListener syncListener) {
		try {
			MyWordSyncResponse response = networkService.synchronousRequest(request);
			handleSyncResponse(response, syncListener);
		} catch (Exception e) {
			Log.e(TAG, "Unable to sync word remotely ", e);
		}
	}

	protected void handleSyncResponse(final MyWordSyncResponse result, final VocabularySyncListener syncListener) {
		if (!result.isSuccess()) {
			if (syncListener != null) {
				syncListener.onError(null);
			}
			Log.e(TAG, "Received FAILURE while synching vocabularies");
			return;
		}
		for (MyWordSyncResult sync : result.getLocalAddUpdateConfirmedByServer()) {
			vocabularyDatabase.updateMyWordAttributes(sync.getClientId(), sync.getServerId(), sync.getServerVersion(), false);
		}

		for (MyWordSyncResult sync : result.getAddedOnServer()) {
			vocabularyDatabase.addMyWord(sync.getMyWord(), sync.getServerId(), sync.getServerVersion(), false, getUser());
		}

		if (CommonUtils.isNotEmpty(result.getLocalRemoveConfirmedByServer())) {
			vocabularyDatabase.confirmMyWordsRemove(result.getLocalRemoveConfirmedByServer());
		}

		for (MyWordSyncResult sync : result.getRemovedOnServer()) {
			vocabularyDatabase.removeMyWordByServerId(sync.getServerId());
		}

		for (MyWordSyncResult sync : result.getUpdatedOnServer()) {
			vocabularyDatabase.updateMyWordByServerId(sync.getMyWord(), sync.getServerId(), sync.getServerVersion());
		}
		if (syncListener != null) {
			syncListener.onVocabularySyncSuccess();
		}
		Log.i(TAG, "Remote word sync success");
	}

	public MyWord getMyWord(final String lemma) {
		return vocabularyDatabase.findMyWord(lemma.trim(), getUser());
	}

	public void removeAllDebug() {
		vocabularyDatabase.clearMyWordsTables();
	}

	private String getAuthToken() {
		UserProfile userProfile = userService.getUserProfile();
		return userProfile.getGuid();
	}

	private User getUser() {
		return userService.getUser();
	}

	public void copyAnonymousToUser(final UserProfile user) {
		AnonymousUser anonymousUser = vocabularyDatabase.findAnonymousUser();
		Collection<MyWord> allAnonymousUserWords = vocabularyDatabase.findAllMyWords(anonymousUser, null);
		if (allAnonymousUserWords.isEmpty()) {
			return;
		}
		for (MyWord myWord : allAnonymousUserWords) {
			MyWordAttributes anonymousAttributes = myWord.getAttributes();
			myWord.invalidateAttributes(); // to copy just data, not metadata
			vocabularyDatabase.addMyWord(myWord, -1, -1, true, user);
			vocabularyDatabase.removeMyWord(anonymousAttributes, anonymousUser);
		}
	}

}

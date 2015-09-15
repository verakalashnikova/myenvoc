package com.myenvoc.android.ui.dictionary;

import java.util.Date;

import javax.inject.Inject;

import com.google.android.gms.ads.AdView;
import com.myenvoc.R;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.android.ui.MyenvocActivity;
import com.myenvoc.android.ui.vocabulary.EditVocabularyActivity;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.VocabularyUtils;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

public class MyenvocDictionaryActivity extends MyenvocActivity {
	private FragmentTabHost tabHost;

	@Inject
	VocabularyService vocabularyService;

	@Inject
	UserService userService;

	private MenuItem addEditMenu;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dictionary_tabs);
/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
*/
		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		tabHost.addTab(tabHost.newTabSpec("definition").setIndicator(getResources().getString(R.string.definition)),
				WordnetDefinitionFragment.class, null);
		tabHost.addTab(tabHost.newTabSpec("translation").setIndicator(getResources().getString(R.string.translation)),
				TranslationFragment.class, null);
		tabHost.addTab(tabHost.newTabSpec("images").setIndicator(getResources().getString(R.string.images)), ImagesFragment.class, null);

		for (int i = 0; i < 3; i++) {
			View view = tabHost.getTabWidget().getChildAt(i);
			view.getLayoutParams().height = 60;
		}

		collectWordFromIntent(getIntent());

		tabHost.setCurrentTab(0);
		installAds((AdView) findViewById(R.id.dictionaryAdView));
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.dictionary_menu, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addEditMenu = menu.findItem(R.id.menu_add_edit_word);
		refreshAddEditIcon();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		case android.R.id.home:
			Intent intent = new Intent(this, MyenvocHomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_add_edit_word:
			handleOnAddEditClick();
			return true;
		default:
			return false;
		}
	}

	String getWordFromIntent() {
		return getIntent().getExtras().getString(CommonUtils.WORD);
	}

	String getTranscriptionFromIntent() {

		return getIntent().getExtras().getString(CommonUtils.TRANSCRIPTION);
	}

	void handleOnAddEditClick() {
		handleOnAddEditClick(getWordFromIntent(), getTranscriptionFromIntent(), null);
	}

	void handleOnAddEditClick(final String word, final String transcription, final String imageUrl) {
		if (userService.getUser().isAnonymous()) {
			VocabularyUtils.warnAnonymousUser(this, new OnClickListener() {

				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					openEditMyWordActivity(word, transcription, imageUrl);
				}
			});
		} else {
			openEditMyWordActivity(word, transcription, imageUrl);
		}
	}

	private void openEditMyWordActivity(final String word, final String transcription, final String imageUrl) {
		/**
		 * refetch again, as it could be actually already added, if I add and
		 * then go back and add again.
		 */
		MyWord myWord = vocabularyService.getMyWord(word);
		Intent editMyWordIntent = new Intent(getBaseContext(), EditVocabularyActivity.class);
		if (myWord == null) {
			MyWord newWord = createNewWord(word, transcription, imageUrl);
			editMyWordIntent.putExtra(CommonUtils.MY_WORD_ID, newWord.getAttributes().getId());
		} else {
			editMyWordIntent.putExtra(CommonUtils.MY_WORD_ID, myWord.getAttributes().getId());
		}
		editMyWordIntent.putExtra(CommonUtils.IS_BLANK, myWord == null);
		startActivity(editMyWordIntent);
	}

	private MyWord createNewWord(final String word, final String transcription, final String imageUrl) {
		MyWord newWord = new MyWord();
		newWord.setAdded(new Date());
		newWord.setLemma(word);
		newWord.setImageUrl(imageUrl);
		newWord.setTranscription(transcription);
		vocabularyService.addOrUpdateMyWord(newWord);
		return newWord;
	}

	public void refreshAddEditIcon() {
		if (addEditMenu == null) {
			return;
		}
		if (getIntent().getExtras().getBoolean(CommonUtils.IS_NEW)) {
			addEditMenu.setIcon(getBaseContext().getResources().getDrawable(R.drawable.ic_new_content));
			addEditMenu.setTitle(R.string.add_to_vocabulary_no_shortage);
		} else {
			addEditMenu.setIcon(getBaseContext().getResources().getDrawable(R.drawable.ic_edit));
			addEditMenu.setTitle(R.string.edit_my_word_no_shortage);
		}
	}

	// <!-- android:launchMode="singleTop" --> was commented out. Otherwise
	// back/forward would have to be implemented by hand
	// @Override
	// protected void onNewIntent(final Intent intent) {
	// getWordFromIntent(intent);
	// }

	private void collectWordFromIntent(final Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			String word = CommonUtils.getLastSegment(intent.getData());
			intent.putExtra(CommonUtils.WORD, word.trim());

		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			intent.putExtra(CommonUtils.WORD, query.trim());

		}
	}

}

package com.myenvoc.android.ui.vocabulary;

import javax.inject.Inject;

import com.google.android.gms.ads.AdView;
import com.myenvoc.R;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.domain.MyWordStatus;
import com.myenvoc.android.service.vocabulary.Vocabulary;
import com.myenvoc.android.service.vocabulary.VocabularyFilter;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.android.service.vocabulary.VocabularyService.MyWordUpdateAction;
import com.myenvoc.android.ui.MyenvocActivity;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;
import com.myenvoc.android.ui.vocabulary.ChangeWordStatusDialog.MyWordStatusListener;
import com.myenvoc.android.ui.vocabulary.FilterVocabularyDialog.FilterVocabularyDialogListener;
import com.myenvoc.android.ui.vocabulary.VocabularyViewDialog.VocabularyViewListener;
import com.myenvoc.android.ui.vocabulary.VocabularyViewStrategyFactory.VocabularyViewStrategy;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.VocabularyUtils;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

public class ViewVocabularyActivity extends MyenvocActivity implements FilterVocabularyDialogListener {

	@Inject
	VocabularyService vocabularyService;

	@Inject
	VocabularyViewStrategyFactory vocabularyViewStrategyFactory;

	private Vocabulary vocabulary;
	private MenuItem filterVocabulary;

	private View myWordView;

	private View prev;

	private View next;

	private TextView vocabularyNumbers;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		VocabularyUtils.warnAnonymousUser(this, null);

		render();

	}

	private void render() {
		VocabularyViewStrategy viewStrategy = vocabularyViewStrategyFactory.getCurrentVocabularyViewStrategy();

		Vocabulary rawVocabulary = vocabularyService.findVocabulary(VocabularyFilter.getCurrentVocabularyFilter(getBaseContext()),
				VocabularyFilter.getCurrentVocabularyOrder(getBaseContext()));

		vocabulary = viewStrategy.massageVocabulary(rawVocabulary);

		vocabulary.tryToMoveToSavedIndex();
		if (vocabulary.hasCurrent()) {
			LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.myWordView = inflater.inflate(viewStrategy.getViewId(), null);
			setContentView(myWordView);
			installAds((AdView) findViewById(R.id.vocabularyAdView));

			TextView wordTitle = (TextView) myWordView.findViewById(R.id.wordTitle);
			ViewMyWordWidget.initializeLemmaTextView(getBaseContext(), wordTitle);

			ScrollView contentView = (ScrollView) myWordView.findViewById(R.id.content);

			viewStrategy.renderVocabularyWord(vocabulary, myWordView, contentView, this);

			addButtons(vocabulary, myWordView, contentView, viewStrategy);

		} else {
			handleNoVocabularyWords();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		}
	}

	private void handleNoVocabularyWords() {
		setContentView(R.layout.empty_vocabulary_or_filter);

		TextView message = (TextView) findViewById(R.id.message);
		if (!VocabularyFilter.filterSet(getBaseContext())) {
			message.setText(getResources().getString(R.string.vocabulary_is_empty));
		} else {
			message.setText(getResources().getString(R.string.no_words_for_filter));
		}
	}

	public void onOpenCard(final View view) {
		VocabularyViewStrategy vocabularyStrategy = vocabularyViewStrategyFactory.getVocabularyViewStrategy(VocabularyView.DEFAULT);

		ScrollView contentView = (ScrollView) myWordView.findViewById(R.id.content);
		vocabularyStrategy.renderVocabularyWord(vocabulary, myWordView, contentView, this);
	}

	private void addButtons(final Vocabulary vocabulary, final View myWordView, final ScrollView contentView,
			final VocabularyViewStrategy viewStrategy) {

		this.prev = myWordView.findViewById(R.id.prevWordButton);
		this.next = myWordView.findViewById(R.id.nextWordButton);
		this.vocabularyNumbers = (TextView) myWordView.findViewById(R.id.vocabularyNumbers);

		updateButtonsState(vocabulary, prev, next);
		renderVocabularyNumbers(vocabulary, vocabularyNumbers);

		prev.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				vocabulary.prev();
				viewStrategy.renderVocabularyWord(vocabulary, myWordView, contentView, ViewVocabularyActivity.this);
				renderVocabularyNumbers(vocabulary, vocabularyNumbers);
				updateButtonsState(vocabulary, prev, next);
			}
		});
		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				vocabulary.next();
				viewStrategy.renderVocabularyWord(vocabulary, myWordView, contentView, ViewVocabularyActivity.this);
				renderVocabularyNumbers(vocabulary, vocabularyNumbers);
				updateButtonsState(vocabulary, prev, next);
			}
		});
	}

	private void renderVocabularyNumbers(final Vocabulary vocabulary, final TextView vocabularyNumbers) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(vocabulary.getCurrentIndex() + 1).append('/').append(vocabulary.getTotalGivenFilter());
		if (vocabulary.hasFilter()) {
			buffer.append(" (").append(vocabulary.getTotalWords()).append(')');
		}
		vocabularyNumbers.setText(buffer.toString());
	}

	private void updateButtonsState(final Vocabulary vocabulary, final View prev, final View next) {
		prev.setEnabled(vocabulary.hasPrev());
		next.setEnabled(vocabulary.hasNext());
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.vocabulary_menu, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

			MenuItem menuItem = menu.findItem(R.id.search);
			SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
			//SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
			//searchView.setIconifiedByDefault(true);
			//searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

			if (vocabulary != null) {
				boolean noWordsAtAllYet = vocabulary.isEmpty() && !VocabularyFilter.filterSet(getBaseContext());
				//searchView.setIconifiedByDefault(!noWordsAtAllYet);
				//searchView.setIconified(!noWordsAtAllYet);
			}
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		MenuItem editWord = menu.findItem(R.id.menu_edit_word);

		editWord.setEnabled(vocabulary.hasCurrent());

		this.filterVocabulary = menu.findItem(R.id.menu_filter_vocabulary);
		handleFilterSelection();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		case R.id.menu_filter_vocabulary:
			onFilterRequested();
			return true;
		case R.id.menu_edit_word:
			onEditRequested();
			return true;
		case R.id.menu_change_status:
			onChangeStatus();
			return true;
		case R.id.menu_delete:
			onDelete();
			return true;
		case R.id.menu_change_view:
			onChangeView();
			return true;
		case android.R.id.home:
			Intent intent = new Intent(this, MyenvocHomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return true;
		default:
			return false;
		}
	}

	private void onDelete() {
		if (!vocabulary.hasCurrent()) {
			return;
		}

		ConfimWordDeleteDialog.showWordDeleteConfirmDialog(this, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface arg0, final int arg1) {
				MyWord myWord = vocabulary.getCurrent();
				vocabularyService.deleteMyWord(myWord.getAttributes().getId());

				render();
			}
		});

	}

	private void onChangeStatus() {
		if (!vocabulary.hasCurrent()) {
			return;
		}

		final MyWord myWord = vocabulary.getCurrent();
		ChangeWordStatusDialog.showDontShowAgainDialog(this, new MyWordStatusListener() {

			@Override
			public void onStatusSelection(final MyWordStatus myWordStatus) {
				if (myWord.getStatus() == myWordStatus) {
					return;
				}

				MyWord updatedWord = vocabularyService.updateMyWord(new MyWordUpdateAction() {

					@Override
					public void run() {
						getFreshWord().setStatus(myWordStatus);
					}
				}, myWord.getAttributes().getId());

				vocabulary.updateCurrent(updatedWord);

			}
		}, myWord.getStatus());
	}

	private void onEditRequested() {
		if (!vocabulary.hasCurrent()) {
			return;
		}

		MyWord myWord = vocabulary.getCurrent();
		Intent editMyWordIntent = new Intent(getBaseContext(), EditVocabularyActivity.class);

		editMyWordIntent.putExtra(CommonUtils.MY_WORD_ID, myWord.getAttributes().getId());
		// editMyWordIntent.putExtra(CommonUtils.VOCABULARY, true);

		startActivity(editMyWordIntent);
		// finish();
	}

	private void onFilterRequested() {
		FilterVocabularyDialog.create(VocabularyFilter.getCurrentVocabularyFilter(getBaseContext()), getSupportFragmentManager());
	}

	private void onChangeView() {
		VocabularyViewDialog.create(this, vocabularyViewStrategyFactory.getVocabularyView(), new VocabularyViewListener() {

			@Override
			public void onViewSelection(final VocabularyView vocabularyView) {
				if (vocabularyViewStrategyFactory.getVocabularyView() != vocabularyView) {
					vocabularyViewStrategyFactory.setVocabularyView(vocabularyView);
					render();
				}
			}

		});
	}

	@Override
	public void onFilterSelected(final DialogFragment dialog, final VocabularyFilter vocabularyFilter) {
		VocabularyFilter.setCurrentVocabularyFilter(vocabularyFilter, getBaseContext());

		handleFilterSelection();

		Vocabulary.flushCurrentWord();

		render();
	}

	private void handleFilterSelection() {
		if (VocabularyFilter.filterSet(getBaseContext())) {
			filterVocabulary.setIcon(R.drawable.ic_filter_selected);
		} else {
			filterVocabulary.setIcon(R.drawable.ic_filter);
		}
	}

	@Override
	protected void onRestart() {
		if (vocabulary != null && vocabulary.hasCurrent()) {
			MyWord refreshed = vocabularyService.findMyWordById(vocabulary.getCurrent().getAttributes().getId());
			if (refreshed == null) {
				vocabulary.removeCurrent();
				updateButtonsState(vocabulary, prev, next);
				renderVocabularyNumbers(vocabulary, vocabularyNumbers);
			} else {
				vocabulary.updateCurrent(refreshed);
			}
		}

		if (vocabulary != null && vocabulary.hasCurrent()) {

			ScrollView contentView = (ScrollView) myWordView.findViewById(R.id.content);

			VocabularyViewStrategy vocabularyStrategy = vocabularyViewStrategyFactory.getCurrentVocabularyViewStrategy();
			vocabularyStrategy.renderVocabularyWord(vocabulary, myWordView, contentView, this);
		} else {
			handleNoVocabularyWords();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				invalidateOptionsMenu();
			}
		}

		super.onRestart();
	}
}

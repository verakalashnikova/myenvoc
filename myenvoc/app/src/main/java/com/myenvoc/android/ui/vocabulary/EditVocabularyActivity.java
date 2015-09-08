package com.myenvoc.android.ui.vocabulary;

import java.util.List;

import javax.inject.Inject;

import com.google.android.gms.ads.AdView;
import com.myenvoc.R;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.domain.MyWordContext;
import com.myenvoc.android.domain.MyWordStatus;
import com.myenvoc.android.service.dictionary.ImageService;
import com.myenvoc.android.service.vocabulary.MyWordInitialDataLoadService;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.android.service.vocabulary.VocabularyService.MyWordUpdateAction;
import com.myenvoc.android.ui.MyenvocActivity;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;
import com.myenvoc.android.ui.dictionary.PickWordInfoActivity;
import com.myenvoc.android.ui.vocabulary.EditMyContextDialog.EditMyContextDialogListener;
import com.myenvoc.android.ui.vocabulary.EditTranslationDialog.EditTranslationDialogListener;
import com.myenvoc.android.ui.vocabulary.LinearDataList.SelectionListener;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.CustomFontsLoader;
import com.myenvoc.commons.StringUtils;
import com.myenvoc.commons.VocabularyUtils;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

public class EditVocabularyActivity extends MyenvocActivity implements EditMyContextDialogListener, EditTranslationDialogListener {

	@Inject
	MyWordInitialDataLoadService myWordInitialDataLoadService;
	@Inject
	ImageService imageService;
	@Inject
	VocabularyService vocabularyService;

	public enum InfoType {
		DEFINITION, TRANSLATION, IMAGE;
		public static final String INFO_TYPE = "infoType";
	}

	/**
	 * don't store myword directly, as it may be changed concurrently from
	 * pooled threads, loading initial values. Thus refetch myword at updates
	 */
	private int myWordId;
	private boolean blank;
	private String lemma;
	private View editMyWordView;
	private DefinitionList definitionList;
	private TranslationList translationList;
	private TextView lemmaTextView;
	private View pronunciationInProgress;

	private final String tag = getClass().getName();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if (savedInstanceState != null) {
			myWordId = savedInstanceState.getInt(CommonUtils.MY_WORD_ID);
			blank = false;
		} else {
			Intent intent = getIntent();
			myWordId = intent.getExtras().getInt(CommonUtils.MY_WORD_ID);
			blank = intent.getExtras().containsKey(CommonUtils.IS_BLANK) && intent.getExtras().getBoolean(CommonUtils.IS_BLANK);
		}

	}

	private void renderEditMyWord(final int myWordId, final boolean blank) {
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View editMyWordView = inflater.inflate(R.layout.edit_my_word, null);
		setContentView(editMyWordView);
		installAds((AdView) findViewById(R.id.editVocabularyAdView));
		this.editMyWordView = editMyWordView;

		lemmaTextView = (TextView) getView().findViewById(R.id.wordTitle);
		pronunciationInProgress = getView().findViewById(R.id.pronunciationInProgress);
		try {
			lemmaTextView.setTypeface(CustomFontsLoader.getTypeface(getBaseContext(), CustomFontsLoader.GENTIUM), Typeface.BOLD);
		} catch (Exception e) {
			Log.e(tag, "", e);
		}

		MyWord myWord = vocabularyService.findMyWordById(myWordId);

		this.lemma = myWord.getLemma();

		if (blank) {
			populateMyWordWidget(myWord);
			myWordInitialDataLoadService.populateMyWord(lemma, myWord, this, MyenvocHomeActivity.getHandler());
		} else {
			populateMyWordWidget(myWord);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_my_word_menu, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		case R.id.menu_delete:
			onDelete();
			return true;
		case android.R.id.home:
			// Bundle extras = getIntent().getExtras();
			// if (returnToVocabulary(extras)) {
			// // came from vocabulary, so return there.
			// // recoverViewVocabularyActivity(this, getIntent());
			// finish();
			// } else {
			// Intent intent = new Intent(this, MyenvocHomeActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
			// }
			finish();
			return true;
		default:
			return false;
		}
	}

	private void onDelete() {
		final FragmentActivity activity = this;
		ConfimWordDeleteDialog.showWordDeleteConfirmDialog(activity, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface arg0, final int arg1) {
				vocabularyService.deleteMyWord(myWordId);
				// recoverViewVocabularyActivity(activity,
				// activity.getIntent());
				activity.finish();
			}
		});

	}

	// public static void recoverViewVocabularyActivity(final FragmentActivity
	// activity, final Intent intent) {
	// Intent vocabularyIntent = new Intent(activity,
	// ViewVocabularyActivity.class);
	//
	// copyVocabularyReturnAddressIfNeeded(intent, vocabularyIntent);
	//
	// activity.startActivity(vocabularyIntent);
	// }

	private void populateMyWordWidget(final MyWord myWord) {
		definitionList = (DefinitionList) getView().findViewById(R.id.definitionList);
		translationList = (TranslationList) getView().findViewById(R.id.translationList);
		setLemmaAndPronounce(myWord);
		addTranslations(myWord);
		setImage(myWord);
		addContexts(myWord);
		addAddedAt(myWord);
		setStatus(myWord);

		installSpinnerListener(myWord);
		installPickImageListener();
		installPickTranslationListener();
		installPickDefinitionListener();

		installAddTranslationListener();
		installAddDefinitionListener();

		installTranslationListListener();
		installMyContextListListener();
	}

	private void installMyContextListListener() {
		definitionList.setSelectionListener(new SelectionListener<MyWordContext>() {

			@Override
			public void onItemSelected(final MyWordContext data, final int index) {
				EditMyContextDialog.edit(data, index, getSupportFragmentManager());
			}

			@Override
			public void onItemRemove(final int index) {
				updateMyWord(new MyWordUpdateAction() {

					@Override
					public void run() {
						VocabularyUtils.removeDefinitionAt(getFreshWord(), index);
					}
				});
				definitionList.removeEntry(index);
			}
		});
	}

	private void installTranslationListListener() {

		translationList.setSelectionListener(new SelectionListener<String>() {

			@Override
			public void onItemSelected(final String data, final int index) {
				EditTranslationDialog.edit(data, index, getSupportFragmentManager());
			}

			@Override
			public void onItemRemove(final int index) {
				updateMyWord(new MyWordUpdateAction() {

					@Override
					public void run() {
						MyWord myWord = VocabularyUtils.removeTranslationAt(getFreshWord(), index);
					}
				});
				translationList.removeEntry(index);
			}
		});
	}

	private void installAddTranslationListener() {
		Button addTranslation = (Button) getView().findViewById(R.id.addTranslation);
		addTranslation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				EditTranslationDialog.create(getSupportFragmentManager());
			}
		});
	}

	private void installAddDefinitionListener() {
		Button addDefinition = (Button) getView().findViewById(R.id.addDefinition);
		addDefinition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				EditMyContextDialog.create(getSupportFragmentManager());
			}
		});
	}

	private void installPickDefinitionListener() {
		Button pickDefinition = (Button) getView().findViewById(R.id.pickDefinition);
		pickDefinition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				pickMyWordInfo(InfoType.DEFINITION);
			}
		});
	}

	private void installPickTranslationListener() {
		Button pickTranslation = (Button) getView().findViewById(R.id.pickTranslation);
		pickTranslation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				pickMyWordInfo(InfoType.TRANSLATION);
			}
		});
	}

	private void installPickImageListener() {
		getImageView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				pickMyWordInfo(InfoType.IMAGE);

			}
		});
	}

	private void setLemmaAndPronounce(final MyWord myWord) {

		lemmaTextView.setText(myWord.getLemma());

		if (StringUtils.isNotEmpty(myWord.getTranscription())) {
			VocabularyUtils.appendTranscription(myWord.getTranscription(), lemmaTextView, getBaseContext());
		}
		lemmaTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				String word = myWord.getLemma();
				VocabularyUtils.playTranscription(word, EditVocabularyActivity.this, pronunciationInProgress);
			}
		});
	}

	private void setStatus(final MyWord myWord) {
		Spinner statusSpinner = (Spinner) getView().findViewById(R.id.status);
		MyWordStatus status = myWord.getStatus();
		if (status != null) {
			int index = status.ordinal();
			statusSpinner.setSelection(index);
		}
	}

	private void installSpinnerListener(final MyWord initial) {
		Spinner statusSpinner = (Spinner) getView().findViewById(R.id.status);
		statusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int index, final long arg3) {
				MyWordStatus[] values = MyWordStatus.values();
				if (index < values.length) {
					final MyWordStatus myWordStatus = values[index];

					if (myWordStatus == initial.getStatus()) {
						return;
					}

					updateMyWord(new MyWordUpdateAction() {

						@Override
						public void run() {
							getFreshWord().setStatus(myWordStatus);
						}
					});
				}
			}

			@Override
			public void onNothingSelected(final AdapterView<?> arg0) {

			}
		});
	}

	private void addAddedAt(final MyWord myWord) {
		TextView addedAtTextView = (TextView) getView().findViewById(R.id.addedAt);
		addedAtTextView.setText(getResources().getString(R.string.added_at) + " "
				+ CommonUtils.formatDate(myWord.getAdded(), getBaseContext()));
	}

	private void addContexts(final MyWord myWord) {
		List<MyWordContext> contexts = myWord.getContexts();
		if (CommonUtils.isNotEmpty(contexts)) {
			for (MyWordContext myWordContext : contexts) {
				addDefinition(myWordContext);
			}
		}
	}

	private void setImage(final MyWord myWord) {
		String imageUrl = myWord.getImageUrl();
		if (CommonUtils.isNotEmpty(imageUrl)) {
			imageService.loadImage(imageUrl, getImageView());
		}
	}

	private void addTranslations(final MyWord myWord) {
		for (String translation : VocabularyUtils.getTranslations(myWord)) {
			if (!"".equals(translation)) {
				addTranslation(translation);
			}
		}
	}

	public void setImageBitmap(final Bitmap data) {
		ImageView image = getImageView();
		image.setImageBitmap(data);
	}

	private ImageView getImageView() {
		return (ImageView) getView().findViewById(com.myenvoc.R.id.image);
	}

	public void updateContext(final MyWordContext myWordContext) {
		DefinitionList definitionList = (DefinitionList) getView().findViewById(R.id.definitionList);
		definitionList.addEntryToBottom(myWordContext);
	}

	private void updateMyWord(final MyWordUpdateAction updateAction) {
		vocabularyService.updateMyWord(updateAction, myWordId);
	}

	private void pickMyWordInfo(final InfoType infoType) {
		Intent selectInfoIntent = new Intent(getBaseContext(), PickWordInfoActivity.class);
		selectInfoIntent.putExtra(CommonUtils.MY_WORD_ID, myWordId);
		selectInfoIntent.putExtra(CommonUtils.MY_WORD_LEMMA, lemma);
		selectInfoIntent.putExtra(InfoType.INFO_TYPE, infoType);

		// copyVocabularyReturnAddressIfNeeded(getIntent(), selectInfoIntent);

		startActivity(selectInfoIntent);
	}

	// public static boolean copyVocabularyReturnAddressIfNeeded(final Intent
	// intent, final Intent copyTo) {
	// Bundle extras = intent.getExtras();
	// if (returnToVocabulary(extras)) {
	// copyTo.putExtra(CommonUtils.VOCABULARY, true);
	// return true;
	// }
	// return false;
	// }
	//
	// public static boolean returnToVocabulary(final Bundle extras) {
	// return extras != null && extras.containsKey(CommonUtils.VOCABULARY);
	// }

	private View getView() {
		return editMyWordView;
	}

	@Override
	public void onAddOrUpdateTranslation(final DialogFragment dialog, final String translation, final int index) {
		if (CommonUtils.isEmpty(translation)) {
			return;
		}

		if (index < 0) {
			// add new translation
			addTranslation(translation);
		} else {
			translationList.updateEntry(translation, index);
		}

		updateMyWord(new MyWordUpdateAction() {
			@Override
			public void run() {
				if (index < 0) {
					VocabularyUtils.addTranslation(getFreshWord(), translation);
				} else {
					VocabularyUtils.setTranslation(getFreshWord(), translation, index);
				}
			}
		});
	}

	@Override
	public void onAddOrUpdateMyContext(final DialogFragment dialog, final MyWordContext myWordContext, final int index) {

		if (index < 0) {
			// add new context
			addDefinition(myWordContext);
		} else {
			definitionList.updateEntry(myWordContext, index);
		}

		updateMyWord(new MyWordUpdateAction() {
			@Override
			public void run() {
				if (index < 0) {
					VocabularyUtils.addContext(getFreshWord(), myWordContext);
				} else {
					VocabularyUtils.setContext(getFreshWord(), myWordContext, index);
				}
			}
		});
	}

	public void addDefinition(final MyWordContext myWordContext) {
		definitionList.addEntryToBottom(myWordContext);
	}

	public void addTranslation(final String translation) {
		translationList.addEntryToBottom(translation);
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putInt(CommonUtils.MY_WORD_ID, myWordId);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		blank = false;
	}

	@Override
	public void onStart() {
		super.onStart();

		renderEditMyWord(myWordId, blank);
	}
}

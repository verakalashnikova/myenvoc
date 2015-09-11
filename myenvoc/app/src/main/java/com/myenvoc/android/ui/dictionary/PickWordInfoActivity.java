package com.myenvoc.android.ui.dictionary;

import com.google.android.gms.ads.AdView;
import com.myenvoc.R;
import com.myenvoc.android.ui.MyenvocActivity;
import com.myenvoc.android.ui.vocabulary.EditVocabularyActivity.InfoType;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

public class PickWordInfoActivity extends MyenvocActivity {
	private FragmentTabHost tabHost;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dictionary_tabs);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		tabHost.addTab(tabHost.newTabSpec("definition").setIndicator(getResources().getString(R.string.definition)),
				PickWordnetDefinitionFragment.class, null);
		tabHost.addTab(tabHost.newTabSpec("translation").setIndicator(getResources().getString(R.string.translation)),
				PickTranslationFragment.class, null);
		tabHost.addTab(tabHost.newTabSpec("images").setIndicator(getResources().getString(R.string.images)), PickImagesFragment.class, null);
		InfoType infoType = (InfoType) getIntent().getExtras().get(InfoType.INFO_TYPE);
		tabHost.setCurrentTab(infoType.ordinal());

		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 60;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 60;
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = 60;
		installAds((AdView) findViewById(R.id.dictionaryAdView));
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simple_search_menu, menu);

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
		case android.R.id.home:
			finish();
			return true;
		default:
			return false;
		}
	}
}

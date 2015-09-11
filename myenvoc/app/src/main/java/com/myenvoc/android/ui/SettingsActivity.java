package com.myenvoc.android.ui;

import com.myenvoc.R;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent vocabularyIntent = new Intent(this, MyenvocHomeActivity.class);
			startActivity(vocabularyIntent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simple_menu, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}
}
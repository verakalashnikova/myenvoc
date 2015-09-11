package com.myenvoc.android.ui;

import android.content.Intent;
import android.os.Bundle;

import com.myenvoc.R;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;

public class AboutActivity extends MyenvocActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}

//	@Override
//	public boolean onOptionsItemSelected(final MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			Intent vocabularyIntent = new Intent(this, MyenvocHomeActivity.class);
//			startActivity(vocabularyIntent);
//			finish();
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(final Menu menu) {
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.simple_menu, menu);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		return true;
//	}

}
package com.myenvoc.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;

import com.example.vera.myapplication.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.myenvoc.R;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;

import java.io.IOException;
import java.util.logging.Logger;

public class AboutActivity extends MyenvocActivity {


	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "qwe"));

	}
	class EndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
		private MyApi myApiService = null;
		private Context context;

		@Override
		protected String doInBackground(Pair<Context, String>... params) {
			if(myApiService == null) {  // Only do this once
				MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
						new AndroidJsonFactory(), null)
						// options for running against local devappserver
						// - 10.0.2.2 is localhost's IP address in Android emulator
						// - turn off compression when running against local devappserver
						//.setRootUrl("http://10.0.2.2:8080/_ah/api/")
						.setRootUrl("http://192.168.1.6:8080/_ah/api/")
						.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
							@Override
							public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
								abstractGoogleClientRequest.setDisableGZipContent(true);
							}
						});
				// end options for devappserver

				myApiService = builder.build();
			}

			context = params[0].first;
			String name = params[0].second;

			try {
				return myApiService.sayHi(name).execute().getData();
			} catch (IOException e) {
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		}
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
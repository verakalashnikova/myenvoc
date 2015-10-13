package com.myenvoc.android.ui;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.appspot.myenvoc.myenvocApi.*;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.myenvoc.R;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;

import java.io.IOException;
import java.util.logging.Logger;

public class AboutActivity extends MyenvocActivity {
    GoogleAccountCredential credential;
    private MyenvocApi service;

    private static final int REQUEST_ACCOUNT_PICKER = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        credential = GoogleAccountCredential.usingAudience(this,
                "server:client_id:1089156366475-5ckkehnm9dn7fb661lurecshj9a8s8bf.apps.googleusercontent.com");

        MyenvocApi.Builder builder = new MyenvocApi.Builder(
                AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                credential);
        builder.setRootUrl("http://192.168.1.6:8080/_ah/api/").setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
            @Override
            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                request.setDisableGZipContent(true);
            }
        });

        service = builder.build();

        chooseAccount();
    }

    void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName =
                            data.getExtras().getString(
                                    AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        new AuthTestAsyncTask().execute(new Pair<Context, String>(this, "first time " + accountName));
                    }
                }
        }
    }

    class AuthTestAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
        private Context context;

        @Override
        protected String doInBackground(Pair<Context, String>... params) {
            context = params[0].first;
            String name = params[0].second;
            try {
                //TODO:: realize what to do with api and return service.sayHi(name).execute().getData();
                return service.signIn().execute().getEmail();//service.sayHi(name).execute().getData();
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }
}

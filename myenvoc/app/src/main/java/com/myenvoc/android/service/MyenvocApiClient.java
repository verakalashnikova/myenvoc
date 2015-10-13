package com.myenvoc.android.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.appspot.myenvoc.myenvocApi.MyenvocApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.FailureValue;
import com.myenvoc.android.inject.ContextAware;
import com.myenvoc.R;
import com.myenvoc.android.service.network.GenericCallback;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by vera on 10/12/15.
 */
@Singleton
public class MyenvocApiClient {
    private final GoogleAccountCredential googleAccountCredential;
    private final boolean localBackend = true;
    private final MyenvocApi myenvocApi;

    @Inject
    MyenvocApiClient(ContextAware contextAware) {
        googleAccountCredential = GoogleAccountCredential.usingAudience(contextAware.getContextFromInstance(),
                contextAware.getContextFromInstance().getResources().getString(R.string.web_client_id));

        MyenvocApi.Builder builder = new MyenvocApi.Builder(
                AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                googleAccountCredential);

        if (localBackend) {
            initForLocal(builder);
        }
        myenvocApi = builder.build();
    }

    private void initForLocal(MyenvocApi.Builder builder) {
        builder.setRootUrl("http://192.168.1.6:8080/_ah/api/").setGoogleClientRequestInitializer
                (new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                        request.setDisableGZipContent(true);
                    }
                });
    }

    public Intent newChooseAccountIntent() {
        return googleAccountCredential.newChooseAccountIntent();
    }

    public void setSelectedAccountName(String accountName) {
        googleAccountCredential.setSelectedAccountName(accountName);
    }

    public MyenvocApi getApi() {
        return myenvocApi;
    }

    /**
     * Makes and API call by invoking {@param apiCall}, and communicating the api call results
     * (both success and failure) to {@param businessCallback}.
     *
     * @param apiCall          the api call callback which returns result of type T
     * @param businessCallback the callback to be called in the main UI thread
     * @param <T>              type of object returned by the api call
     */
    public static <T> void makeApiCall(final Callable<T> apiCall, final GenericCallback<T> businessCallback) {
        new AsyncTask<Void, Void, T>() {
            Exception lastError;

            @Override
            protected T doInBackground(Void... params) {
                try {
                    return apiCall.call();
                } catch (Exception e) {
                    e.printStackTrace();
                    lastError = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(T result) {
                if (lastError == null) {
                    businessCallback.onSuccess(result);
                } else {
                    // TODO: Convert from lastError (Exception) to human redable Failure
                    // For now just show generic INTERNAL_ERROR_CODE
                    businessCallback.onError(Failure.byValue(FailureValue.INTERNAL_ERROR_CODE));
                }
            }
        }.execute();
    }
}

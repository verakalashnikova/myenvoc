package com.myenvoc.android.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Key;
import com.myenvoc.android.ui.dictionary.ToastAdListener;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.util.RoboContext;


public abstract class MyenvocActivity extends AppCompatActivity implements RoboContext {
    protected AdView adView;
    private Tracker mTracker;

    protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();


    protected void installAds(final AdView adView) {
        if (adView == null) {
            return;
        }
        this.adView = adView;
        adView.setAdListener(new ToastAdListener(this, adView));
        // adView.loadAd(new
        // AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("9B55BCAC8E3B3F3AD377B480B86F1F3C")
        // .build());
        adView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RoboGuice.getInjector(this).injectMembersWithoutViews(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        RoboGuice.getInjector(this).injectViewMembers(this);
    }


    @Override
    protected void onDestroy() {
        RoboGuice.destroyInjector(this);

        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


    @Override
    public void onStart() {
        super.onStart();
        //EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
}

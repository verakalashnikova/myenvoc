package com.myenvoc.android.ui;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.myenvoc.android.ui.dictionary.ToastAdListener;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import roboguice.RoboGuice;

public abstract class MyenvocActivity extends AppCompatActivity {
    protected AdView adView;
    private Tracker mTracker;

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
}

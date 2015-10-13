package com.myenvoc.android.ui.dictionary;

import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.UserProfile;
import com.myenvoc.android.service.MyenvocApiClient;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.user.User;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.android.ui.AboutActivity;
import com.myenvoc.android.ui.AppRater;
import com.myenvoc.android.ui.MyenvocActivity;
import com.myenvoc.android.ui.SettingsActivity;
import com.myenvoc.android.ui.user.SignOutDialog;
import com.myenvoc.android.ui.vocabulary.ViewVocabularyActivity;
import com.myenvoc.commons.VocabularyUtils;

import javax.inject.Inject;

public class MyenvocHomeActivity extends MyenvocActivity {

    @Inject
    VocabularyService vocabularyService;

    @Inject
    UserService userService;

    @Inject
    MyenvocApiClient myenvocApiClient;

    private static volatile Handler HANDLER;

    private static final int REQUEST_ACCOUNT_PICKER = 2;

    /**
     * Make sure that this method gets invoked from UI thread.
     *
     * @return
     */
    public static Handler getHandler() {
        if (HANDLER == null) {
            HANDLER = new Handler();
        }
        return HANDLER;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userService.loadUserAndSyncVocabularyOnce();

        AppRater.app_launched(this);

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View homeView = inflater.inflate(R.layout.home_page, null);
        TextView welcomeTextView = (TextView) homeView.findViewById(R.id.welcome);
        Button signInButton = (Button) homeView.findViewById(R.id.signIn);

        User user = userService.getUser();
        if (user.isAnonymous()) {
            welcomeTextView.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            signInButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View arg0) {
                    // TODO: remove SignInActivity
                    startActivityForResult(myenvocApiClient.newChooseAccountIntent(),
                            REQUEST_ACCOUNT_PICKER);
                }
            });
        } else {
            signInButton.setVisibility(View.GONE);
            welcomeTextView.setVisibility(View.VISIBLE);

            UserProfile registeredUser = userService.getUserProfile();
            welcomeTextView.setText(VocabularyUtils.getUserName(registeredUser));
            welcomeTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View arg0) {
                    onLogout();
                }

            });
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        // sV.setSearchableInfo(searchable);

        // SearchView searchView = (SearchView)
        // homeView.findViewById(R.id.xxx);
        // searchView.setIconifiedByDefault(false);

        LinearLayout searchViewWrapper = (LinearLayout) homeView.findViewById(R.id.searchViewWrapper);
        SearchView searchView = new SearchView(getBaseContext());
        searchView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchViewWrapper.addView(searchView);



/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchView searchView = new SearchView(getBaseContext());
			searchView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);

			searchViewWrapper.addView(searchView);
		} else {

			SearchView searchViewSherlock = (SearchView) View.inflate(
					getBaseContext(), R.layout.search_view_sherlock, null);
			searchViewSherlock.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchViewSherlock.setIconifiedByDefault(false);

			searchViewWrapper.addView(searchViewSherlock);
		}
*/
        installAds((AdView) homeView.findViewById(R.id.adView));
        setContentView(homeView);

        //handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);

        User user = userService.getUser();
        MenuItem logout = menu.findItem(R.id.menu_logout);
        if (logout != null) {
            logout.setVisible(!user.isAnonymous());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_about:
                onAbout();
                return true;
            case R.id.menu_settings:
                onSettings();
                return true;
            case R.id.menu_logout:
                onLogout();
                return true;
            case R.id.menu_share:
                onShare();
                return true;
            default:
                return false;
        }
    }

    public void onMyVocabulary(final View view) {
        Intent vocabularyIntent = new Intent(this, ViewVocabularyActivity.class);
        startActivity(vocabularyIntent);
    }

    public void onSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);

        startActivity(settingsIntent);
    }

    public void onAbout() {
        startActivity(new Intent(this, AboutActivity.class));

    }

    public void onShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = getResources().getString(R.string.shareBody);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.shareSubject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.shareVia)));
    }

    private void onLogout() {
        SignOutDialog.create(MyenvocHomeActivity.this.getSupportFragmentManager());
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
                        myenvocApiClient.setSelectedAccountName(accountName);

                        userService.signInV2(accountName, new GenericCallback<com.appspot.myenvoc.myenvocApi.model.UserProfile>() {
                            @Override
                            public void onSuccess(com.appspot.myenvoc.myenvocApi.model.UserProfile data) {
                                Toast.makeText(MyenvocHomeActivity.this.getBaseContext(), R.string.signInSuccess, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(Failure failure) {
                                Toast.makeText(getBaseContext(),
                                        failure.getLocalized(MyenvocHomeActivity.this.getBaseContext()), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        }
    }
}

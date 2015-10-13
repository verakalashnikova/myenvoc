package com.myenvoc.android.service.user;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Pair;
import android.widget.Toast;

import com.appspot.myenvoc.myenvocApi.MyenvocApi;
import com.myenvoc.R;
import com.myenvoc.android.dao.dictionary.VocabularyDatabase;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.FailureValue;
import com.myenvoc.android.domain.JSONConverter;
import com.myenvoc.android.domain.UserProfile;
import com.myenvoc.android.inject.BaseResource;
import com.myenvoc.android.service.MyenvocApiClient;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.service.network.NetworkService;
import com.myenvoc.android.service.network.Resource;
import com.myenvoc.android.service.network.ServerRequest;
import com.myenvoc.android.service.network.ServerRequest.RequestType;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.android.service.vocabulary.VocabularyService.VocabularySyncListener;
import com.myenvoc.commons.MyenvocException;
import com.myenvoc.commons.StringUtils;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import static com.myenvoc.android.service.MyenvocApiClient.makeApiCall;

public class UserService {
    MyenvocApi service;

    private static final String USER_GUID = "userGUID";

    private final Resource signIn;
    private final Resource signUp;
    private final Resource modify;

    @Inject
    NetworkService networkService;

    @Inject
    VocabularyDatabase vocabularyDatabase;

    @Inject
    VocabularyService vocabularyService;

    @Inject
    MyenvocApiClient myenvocApi;

    private User user;

    private final String TAG = UserService.class.getName();

    @Inject
    public UserService(@BaseResource final Resource baseResource) {
        signIn = baseResource.path("pub/signin");
        signUp = baseResource.path("pub/signup");
        modify = baseResource.path("pub/modifyUserProfile");
    }

    public User getUser() {
        if (user == null) {
            // if app gets restarted by Android OS, I should reinitialize all
            // variables - Android really embraces stateless architecture
            user = vocabularyDatabase.findCurrentUser();
        }
        return user;
    }

    public void loadUserAndSyncVocabularyOnce() {
        if (user != null) {
            return;
        }
        user = vocabularyDatabase.findCurrentUser();
        if (!user.isAnonymous()) {
            vocabularyService.syncRemote(getUserProfile(), null);
        }
    }

    public UserProfile getUserProfile() {
        User user = getUser();
        if (user == null || user.isAnonymous()) {
            throw new MyenvocException("Registered user was not found");
        }
        return (UserProfile) user;
    }

    public String getAuthToken() {

        UserProfile userProfile = getUserProfile();
        if (!userProfile.isAnonymous()) {
            return userProfile.getGuid();
        }
        return null;
    }

    public void signOut(final Context context) {
        user = vocabularyDatabase.findAnonymousUser();
        vocabularyDatabase.setCurrentUser(user);
    }

    public void signIn(final String email, final String password, final Handler handler, final Context context,
                       final GenericCallback<UserProfile> businessCallback) {
        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword(password);

        ServerRequest serverRequest = signIn.requestBuilder(RequestType.POST).withPOSTParameter(JSONConverter.convertToString(profile))
                .buildFor(UserProfile.class);
        networkService.asynchronousPOSTRequest(serverRequest, handleUserSignInUp(handler, context, businessCallback));
    }

    public void signInV2(String accountName,
                         GenericCallback<com.appspot.myenvoc.myenvocApi.model.UserProfile> businessCallback) {

        makeApiCall(new Callable<com.appspot.myenvoc.myenvocApi.model.UserProfile>() {
            @Override
            public com.appspot.myenvoc.myenvocApi.model.UserProfile call() throws Exception {
                return myenvocApi.getApi().signIn().execute();
            }
        }, businessCallback);
    }


    public void signUp(final String email, final String password, final String first, final String last, final String lang,
                       final Handler handler, final Context context, final GenericCallback<UserProfile> businessCallback) {
        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword(password);
        profile.setFirstName(first);
        profile.setLastName(last);
        profile.setNativeLanguage(lang);

        ServerRequest serverRequest = signUp.requestBuilder(RequestType.POST).withPOSTParameter(JSONConverter.convertToString(profile))
                .buildFor(UserProfile.class);
        networkService.asynchronousPOSTRequest(serverRequest, handleUserSignInUp(handler, context, businessCallback));
    }

    private GenericCallback<UserProfile> handleUserSignInUp(final Handler handler, final Context context,
                                                            final GenericCallback<UserProfile> businessCallback) {
        return new GenericCallback<UserProfile>() {

            /** in bg thread **/
            @Override
            public void onSuccess(final UserProfile user) {

                final UserProfile signedUpUser = updateUser(user);

                vocabularyService.copyAnonymousToUser(signedUpUser);
                vocabularyService.syncRemote(signedUpUser, new VocabularySyncListener() {

                    @Override
                    public void onVocabularySyncSuccess() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, R.string.vocabularySynced, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(final Failure failure) {
                    }
                });

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        businessCallback.onSuccess(signedUpUser);
                    }
                });

            }

            @Override
            public void onError(final Failure failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        businessCallback.onError(failure == null ? Failure.byValue(FailureValue.GENERAL_ERROR) : failure);
                    }
                });
            }
        };
    }

    public void onOpenIDLoginSuccess(final String guid, final String first, final String last, final String email, final String lang,
                                     final GenericCallback<UserProfile> callback, final Handler handler, final Context context) {

        UserProfile userProfile = vocabularyDatabase.findUserProfile(guid);
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setGuid(guid);
            userProfile.setOpenId(true);
        }
        userProfile.setEmail(email);
        userProfile.setFirstName(first);
        userProfile.setLastName(last);
        userProfile.setNativeLanguage(lang);

        handleUserSignInUp(handler, context, callback).onSuccess(userProfile);

    }

    public void onOpenIDLoginError(final String errorMessage, final GenericCallback<UserProfile> callback, final Handler handler,
                                   final Context context) {

        handleUserSignInUp(handler, context, callback).onError(
                StringUtils.isEmpty(errorMessage) ? null : Failure.byLocalizedValue(errorMessage));
    }

    public void modifyUserProfile(final String guid, final String emailValue, final String pwdValue, final String firstValue,
                                  final String lastValue, final String lang, final Handler handler, final Context context,
                                  final GenericCallback<UserProfile> businessCallback) {
        UserProfile profile = new UserProfile();
        profile.setEmail(emailValue);
        profile.setGuid(guid);
        profile.setPassword(pwdValue);
        profile.setFirstName(firstValue);
        profile.setLastName(lastValue);
        profile.setNativeLanguage(lang);

        ServerRequest serverRequest = modify.requestBuilder(RequestType.POST).withPOSTParameter(JSONConverter.convertToString(profile))
                .buildFor(UserProfile.class);
        networkService.asynchronousPOSTRequest(serverRequest, new GenericCallback<UserProfile>() {

            /** in bg thread **/
            @Override
            public void onSuccess(final UserProfile user) {

                final UserProfile signedUpUser = updateUser(user);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        businessCallback.onSuccess(signedUpUser);
                    }
                });

            }

            @Override
            public void onError(final Failure failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        businessCallback.onError(failure);
                    }
                });
            }
        });
    }

    private UserProfile updateUser(final UserProfile user) {
        vocabularyDatabase.saveOrUpdateUser(user);
        final UserProfile signedUpUser = vocabularyDatabase.findUserProfile(user.getGuid());

        vocabularyDatabase.setCurrentUser(signedUpUser);
        UserService.this.user = signedUpUser;
        return signedUpUser;
    }
}

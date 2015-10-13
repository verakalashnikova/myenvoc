package com.myenvoc.backend.service.auth;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.users.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.googlecode.objectify.Key;
import com.myenvoc.backend.dao.auth.UserDao;
import com.myenvoc.backend.domain.auth.UserProfile;

import java.util.List;

import javax.inject.Inject;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.myenvoc.backend.Ids.ANDROID_AUDIENCE;
import static com.myenvoc.backend.Ids.ANDROID_CLIENT_ID;
import static com.myenvoc.backend.Ids.WEB_CLIENT_ID;

/**
 * Authorization service.
 */
@Api(
        name = "myenvocApi",
        version = "v1",
        clientIds = {WEB_CLIENT_ID, ANDROID_CLIENT_ID},
        audiences = {ANDROID_AUDIENCE}
)
public class AuthEndpoint {

    private final UserDao userDao;

    public AuthEndpoint() {
        // Never called
        userDao = null;
    }

    @Inject
    public AuthEndpoint(UserDao userDao) {
        this.userDao = userDao;
    }

    @ApiMethod(name = "signIn")
    public UserProfile signIn(User user) {
        user = AuthenticationUtils.checkSignedIn(user);

        List<UserProfile> list = userDao.findByEmail(user.getEmail());

        Preconditions.checkArgument(list.size() <= 1, "Unexpected number of users with the given email");

        // The user with the email already exists - just return it
        if (list.size() == 1) {
            UserProfile existingUser = Iterables.getOnlyElement(list);
            return existingUser;
        }

        // Save the user
        UserProfile profile = new UserProfile(user.getEmail());
        userDao.save(profile);
        return profile;
    }


}

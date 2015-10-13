package com.myenvoc.backend.service.auth;

import com.google.appengine.api.users.User;

/**
 * Contains misc utility methods pertaining to authentication checks.
 */
public class AuthenticationUtils {
    public static User checkSignedIn(User user) {
        if (user == null) {
            throw new AuthRequiredException("Resource requires authentication");
        }
        return user;
    }
}

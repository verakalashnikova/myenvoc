package com.myenvoc.backend.dao.auth;

import com.googlecode.objectify.Key;
import com.myenvoc.backend.domain.auth.UserProfile;

import java.util.List;

import javax.inject.Singleton;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by vera on 10/12/15.
 */
@Singleton
public class UserDao {
    public Key<UserProfile> save(UserProfile profile) {
        return ofy().save().entity(profile).now();
    }

    public List<UserProfile> findByEmail(String email) {
        return ofy().load().type(UserProfile.class).filter("email = ", email).list();
    }
}

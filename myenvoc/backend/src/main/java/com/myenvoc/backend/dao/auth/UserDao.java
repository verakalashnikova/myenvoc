package com.myenvoc.backend.dao.auth;

import com.myenvoc.backend.dao.AbstractDao;
import com.myenvoc.backend.domain.auth.UserProfile;

import java.util.List;

import javax.inject.Singleton;

/**
 * Created by vera on 10/12/15.
 */
@Singleton
public class UserDao extends AbstractDao<UserProfile> {
    @Override
    protected Class<UserProfile> registerDomain() {
        return UserProfile.class;
    }

    @Override
    public UserProfile save(UserProfile userProfile) {
        return super.save(userProfile);
    }

    public List<UserProfile> findByEmail(String email) {
        return find("email = ", email);
    }
}

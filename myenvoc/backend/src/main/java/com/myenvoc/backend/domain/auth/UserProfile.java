package com.myenvoc.backend.domain.auth;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.myenvoc.backend.domain.DomainEntity;

@Entity
public class UserProfile extends DomainEntity {
    @Index
    private final String email;

    public UserProfile(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
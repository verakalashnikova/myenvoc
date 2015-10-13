package com.myenvoc.backend.inject;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.myenvoc.backend.domain.auth.UserProfile;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by vera on 10/12/15.
 */
public class BackendModule implements Module {
    @Override
    public void configure(Binder binder) {
        // TODO: add custom bindings here
    }
}

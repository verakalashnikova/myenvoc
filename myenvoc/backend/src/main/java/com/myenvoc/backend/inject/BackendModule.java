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
        binder.bind(Void.class).toProvider(ObjectifyInitializerProvider.class).asEagerSingleton();
    }

    /**
     * Registry for all objectify domain objects.
     */
    static class ObjectifyInitializerProvider implements Provider<Void> {

        @Override
        public Void get() {
            ofy().factory().register(UserProfile.class);
            return null;
        }
    }
}

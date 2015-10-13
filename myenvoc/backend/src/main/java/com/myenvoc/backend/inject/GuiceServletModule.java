package com.myenvoc.backend.inject;

import  com.google.api.server.spi.guice.GuiceSystemServiceServletModule;
import com.myenvoc.backend.service.auth.AuthEndpoint;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vera on 10/12/15.
 */
public class GuiceServletModule extends GuiceSystemServiceServletModule {

    @Override
    protected void configureServlets() {
        super.configureServlets();

        // List all endpoints
        Set<Class<?>> serviceClasses = new HashSet<>();
        serviceClasses.add(AuthEndpoint.class);
        this.serveGuiceSystemServiceServlet("/_ah/spi/*", serviceClasses);
    }
}
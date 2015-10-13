package com.myenvoc.backend.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vera on 10/12/15.
 */
public class GuiceContextListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(new GuiceServletModule());
        moduleList.add(new BackendModule());
        return Guice.createInjector(moduleList);
    }
}
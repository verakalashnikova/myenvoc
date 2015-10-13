package com.myenvoc.backend.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by dkalashnikov on 10/13/15.
 */
public abstract class AbstractDao<T> {
    private boolean domainRegistered;
    private String kind;

    public T save(T entity) {
        persistence().save().entities(entity).now();
        return entity;
    }

    public List<T> find(String condition, Object value) {
        return (List<T>) persistence().load().kind(kind).filter(condition, value).list();
    }

    protected Objectify persistence() {
        registerIfNeeded();
        return ofy();
    }

    private void registerIfNeeded() {
        if (domainRegistered) {
            return;
        }
        ofy().factory().register(registerDomain());
        this.kind = Key.getKind(registerDomain());
    }

    protected abstract Class<T> registerDomain();
}

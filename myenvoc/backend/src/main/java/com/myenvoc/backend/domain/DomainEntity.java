package com.myenvoc.backend.domain;

import com.googlecode.objectify.annotation.Id;

/**
 * Base class for all domain entities.
 */
public class DomainEntity {
    @Id
    private Long id;

    public Long getId() {
        return id;
    }
}

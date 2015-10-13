package com.myenvoc.backend.service.auth;

/**
 * Created by vera on 10/12/15.
 */
public class AuthRequiredException extends RuntimeException {
    public AuthRequiredException(String message) {
        super(message);
    }
}

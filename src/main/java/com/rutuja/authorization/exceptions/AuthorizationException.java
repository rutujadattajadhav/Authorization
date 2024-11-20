package com.rutuja.authorization.exceptions;

public class AuthorizationException {
    public AuthorizationException(String error) {
        this.error = error;
    }
    private String error;
}

package com.bsg.trustedone.exception;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String title, String message) {
        super(title, message);
    }
}

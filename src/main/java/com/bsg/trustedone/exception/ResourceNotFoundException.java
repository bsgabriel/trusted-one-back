package com.bsg.trustedone.exception;

import java.util.List;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message, List<String> errors) {
        super(message, errors);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

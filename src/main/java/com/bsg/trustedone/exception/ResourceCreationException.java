package com.bsg.trustedone.exception;

import java.util.List;

public class ResourceCreationException extends BaseException {

    public ResourceCreationException(String message, List<String> errors) {
        super(message, errors);
    }

    public ResourceCreationException(String message) {
        super(message);
    }
}

package com.bsg.trustedone.exception;

import java.util.List;

public class ResourceAlreadyExistsException extends BaseException {

    public ResourceAlreadyExistsException(String message, List<String> errors) {
        super(message, errors);
    }

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}

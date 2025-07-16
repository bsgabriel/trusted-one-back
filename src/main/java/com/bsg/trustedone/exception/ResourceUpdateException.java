package com.bsg.trustedone.exception;

import java.util.List;

public class ResourceUpdateException extends BaseException {

    public ResourceUpdateException(String message, List<String> errors) {
        super(message, errors);
    }
}

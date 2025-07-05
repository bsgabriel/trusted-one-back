package com.bsg.trustedone.exception;

import java.util.List;

public class GroupCreationException extends BaseValidationException {

    public GroupCreationException(String message, List<String> errors) {
        super(message, errors);
    }
}

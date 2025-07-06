package com.bsg.trustedone.exception;

import java.util.List;

public class GroupUpdateException extends BaseValidationException {

    public GroupUpdateException(String message, List<String> errors) {
        super(message, errors);
    }
}

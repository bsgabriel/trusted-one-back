package com.bsg.trustedone.exception;

import java.util.List;

public class AccountCreationException extends BaseValidationException {

    public AccountCreationException(String message, List<String> errors) {
        super(message, errors);
    }

}

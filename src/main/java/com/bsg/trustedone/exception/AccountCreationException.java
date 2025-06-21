package com.bsg.trustedone.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class AccountCreationException extends RuntimeException {

    private final List<String> errors;

    public AccountCreationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

}

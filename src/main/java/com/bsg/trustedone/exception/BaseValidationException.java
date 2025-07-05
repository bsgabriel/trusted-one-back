package com.bsg.trustedone.exception;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class BaseValidationException extends RuntimeException {

    private final List<String> errors;

    protected BaseValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}


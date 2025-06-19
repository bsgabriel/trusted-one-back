package com.bsg.trustedone.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class UserLoginException extends RuntimeException {

    private final List<String> errors;

    protected UserLoginException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

}

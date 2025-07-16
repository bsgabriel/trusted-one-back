package com.bsg.trustedone.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class BaseException extends RuntimeException {

    private final List<String> errors;

    public BaseException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public BaseException(String message) {
        this(message, new ArrayList<>());
    }

}


package com.bsg.trustedone.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private final String title;

    public BaseException(String title, String message) {
        super(message);
        this.title = title;
    }

}


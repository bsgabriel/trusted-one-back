package com.bsg.trustedone.exception;

public class EmailException extends Exception {

    public EmailException(String message) {
        super(message);
    }

    public EmailException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

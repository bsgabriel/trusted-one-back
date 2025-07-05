package com.bsg.trustedone.exception;


import lombok.Getter;

import java.util.List;

@Getter
public class GroupCreationException extends RuntimeException {

    private final List<String> errors;

    public GroupCreationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

}
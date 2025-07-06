package com.bsg.trustedone.util;

import com.bsg.trustedone.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.util.CollectionUtils.isEmpty;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> invalidCredential() {
        var detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setTitle("An error ocurred on login");
        detail.setDetail("Invalid email or password");

        return createResponseEntity(detail);
    }

    @ExceptionHandler(AccountCreationException.class)
    public ResponseEntity<ProblemDetail> handleAccountCreationException(AccountCreationException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("An error ocurred while creating account");
        detail.setDetail(ex.getMessage());

        if (!isEmpty(ex.getErrors())) {
            detail.setProperty("errors", ex.getErrors());
        }

        return createResponseEntity(detail);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("An error ocurred while creating account");
        detail.setDetail(ex.getMessage());

        return createResponseEntity(detail);
    }

    @ExceptionHandler(UserLoginException.class)
    public ResponseEntity<ProblemDetail> handleUserLoginException(UserLoginException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("An error ocurred on login");
        detail.setDetail(ex.getMessage());

        if (!isEmpty(ex.getErrors())) {
            detail.setProperty("errors", ex.getErrors());
        }

        return createResponseEntity(detail);
    }

    @ExceptionHandler(GroupAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleGroupAlreadyExistsException(GroupAlreadyExistsException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("An error occurred while creating group");
        detail.setDetail(ex.getMessage());

        return createResponseEntity(detail);
    }

    @ExceptionHandler(GroupCreationException.class)
    public ResponseEntity<ProblemDetail> handleGroupCreationException(GroupCreationException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("An error occurred while creating group");
        detail.setDetail(ex.getMessage());

        if (!isEmpty(ex.getErrors())) {
            detail.setProperty("errors", ex.getErrors());
        }

        return createResponseEntity(detail);
    }

    @ExceptionHandler(GroupUpdateException.class)
    public ResponseEntity<ProblemDetail> handleGroupUpdateException(GroupUpdateException ex) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("An error occurred while updating group");

        if (!isEmpty(ex.getErrors())) {
            detail.setProperty("errors", ex.getErrors());
        }

        return createResponseEntity(detail);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "User not authorized");
        detail.setTitle(ex.getMessage());
        return createResponseEntity(detail);
    }


    private ResponseEntity<ProblemDetail> createResponseEntity(ProblemDetail problemDetail) {
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }


}

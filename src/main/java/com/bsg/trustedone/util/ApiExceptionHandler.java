package com.bsg.trustedone.util;

import com.bsg.trustedone.exceptions.AccountCreationException;
import com.bsg.trustedone.exceptions.UserAlreadyRegisteredException;
import com.bsg.trustedone.exceptions.UserLoginException;
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
    public ResponseEntity<String> invalidCredential() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @ExceptionHandler(AccountCreationException.class)
    public ResponseEntity<ProblemDetail> handleAccountCreationException(AccountCreationException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("An error ocurred while creating account");
        detail.setDetail(ex.getMessage());

        if (!isEmpty(ex.getErrors())) {
            detail.setProperty("errors", ex.getErrors());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("An error ocurred while creating account");
        detail.setDetail(ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
    }

    @ExceptionHandler(UserLoginException.class)
    public ResponseEntity<ProblemDetail> handleUserLoginException(UserLoginException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("An error ocurred on login");
        detail.setDetail(ex.getMessage());

        if (!isEmpty(ex.getErrors())) {
            detail.setProperty("errors", ex.getErrors());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

}

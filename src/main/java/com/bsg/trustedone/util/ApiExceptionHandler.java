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

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        var detail = createProblemDetail(HttpStatus.CONFLICT, "Resource already exists", ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceCreationException.class)
    public ResponseEntity<ProblemDetail> handleResourceCreationException(ResourceCreationException ex) {
        var detail = createProblemDetail(HttpStatus.BAD_REQUEST, "An error ocurred while creating resource", ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceUpdateException.class)
    public ResponseEntity<ProblemDetail> handleResourceUpdateException(ResourceUpdateException ex) {
        var detail = createProblemDetail(HttpStatus.BAD_REQUEST, "An error occurred while updating group", ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "User not authorized");
        detail.setTitle(ex.getMessage());
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex) {
        var detail = createProblemDetail(HttpStatus.NOT_FOUND, "Resource not found", ex);
        return createResponseEntity(detail);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, BaseException ex) {
        var detail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        detail.setTitle(title);

        if (!isEmpty(ex.getErrors())) {
            detail.setProperty("errors", ex.getErrors());
        }

        return detail;
    }

    private ResponseEntity<ProblemDetail> createResponseEntity(ProblemDetail problemDetail) {
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }


}

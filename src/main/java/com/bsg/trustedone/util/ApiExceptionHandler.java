package com.bsg.trustedone.util;

import com.bsg.trustedone.exception.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> invalidCredential(BadCredentialsException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setTitle("Não foi possível fazer login");
        detail.setDetail("E-mail ou senha inválidos.");
        detail.setProperty("errorCode", "INVALID_CREDENTIALS");
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        var detail = createProblemDetail(HttpStatus.CONFLICT,ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceCreationException.class)
    public ResponseEntity<ProblemDetail> handleResourceCreationException(ResourceCreationException ex) {
        var detail = createProblemDetail(HttpStatus.BAD_REQUEST, ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceUpdateException.class)
    public ResponseEntity<ProblemDetail> handleResourceUpdateException(ResourceUpdateException ex) {
        var detail = createProblemDetail(HttpStatus.BAD_REQUEST, ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex) {
        var detail = createProblemDetail(HttpStatus.NOT_FOUND, ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        detail.setTitle("Validation failed");
        detail.setDetail("Please verify that all required fields are correctly filled");

        var errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(StringUtils::isNotBlank)
                .toList();

        if(!CollectionUtils.isEmpty(errors)){
            detail.setProperty("errors", errors);
        }

        return createResponseEntity(detail);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, BaseException ex) {
        var detail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        detail.setTitle(ex.getTitle());

        return detail;
    }

    private ResponseEntity<ProblemDetail> createResponseEntity(ProblemDetail problemDetail) {
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

}

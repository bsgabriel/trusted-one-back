package com.bsg.trustedone.util;

import com.bsg.trustedone.exception.*;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> invalidCredential(BadCredentialsException ex) {
        log.error("Could not login", ex);
        var detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setTitle("Não foi possível fazer login");
        detail.setDetail("E-mail ou senha inválidos.");
        detail.setProperty("errorCode", "INVALID_CREDENTIALS");
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        log.error("Resource already exists", ex);
        var detail = createProblemDetail(HttpStatus.CONFLICT,ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceCreationException.class)
    public ResponseEntity<ProblemDetail> handleResourceCreationException(ResourceCreationException ex) {
        log.error("Failed to create resource", ex);
        var detail = createProblemDetail(HttpStatus.BAD_REQUEST, ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceUpdateException.class)
    public ResponseEntity<ProblemDetail> handleResourceUpdateException(ResourceUpdateException ex) {
        log.error("Failed to update resource", ex);
        var detail = createProblemDetail(HttpStatus.BAD_REQUEST, ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found", ex);
        var detail = createProblemDetail(HttpStatus.NOT_FOUND, ex);
        return createResponseEntity(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation failed", ex);
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        detail.setTitle("Dados inválidos");
        detail.setDetail("Verifique os campos e tente novamente");

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

    @ExceptionHandler(SessionException.class)
    public ResponseEntity<ProblemDetail> handleSessionExpiredException(SessionException ex) {
        log.error("Session error", ex);
        var detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setTitle(ex.getTitle());
        detail.setDetail(ex.getMessage());
        detail.setProperty("errorCode", "SESSION_EXPIRED");
        return createResponseEntity(detail);
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<ProblemDetail> handlePasswordResetException(PasswordResetException ex) {
        log.error("Password reset", ex);
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle(ex.getTitle());
        detail.setDetail(ex.getMessage());
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

package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.AccountCreationDto;
import com.bsg.trustedone.dto.UserLoginDto;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.exception.UserLoginException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final Validator validator;

    public void validateRegistrationData(AccountCreationDto accountCreationDto) {
        var violations = validator.validate(accountCreationDto);
        if (violations.isEmpty()) {
            return;
        }

        var errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        throw new ResourceCreationException("Invalid data for account creation", errors);
    }

    public void validateLoginData(UserLoginDto userLoginDto) {
        var violations = validator.validate(userLoginDto);
        if (violations.isEmpty()) {
            return;
        }

        var errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        throw new UserLoginException("Invalid data", errors);
    }

}

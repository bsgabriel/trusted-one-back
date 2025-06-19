package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.RegisterRequestDto;
import com.bsg.trustedone.exceptions.AccountCreationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final Validator validator;

    public void validateRegistrationData(RegisterRequestDto registerData) {
        var violations = validator.validate(registerData);
        if (violations.isEmpty()) {
            return;
        }

        var errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        throw new AccountCreationException("Invalid data for account creation", errors);
    }

}

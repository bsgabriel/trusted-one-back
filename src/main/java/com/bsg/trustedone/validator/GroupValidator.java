package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.GroupCreationDto;
import com.bsg.trustedone.exception.AccountCreationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupValidator {
    private final Validator validator;

    public void validateGroup(GroupCreationDto groupCreationDto) {
        var violations = validator.validate(groupCreationDto);
        if (violations.isEmpty()) {
            return;
        }

        var errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        throw new AccountCreationException("Invalid data for group creation", errors);
    }

}

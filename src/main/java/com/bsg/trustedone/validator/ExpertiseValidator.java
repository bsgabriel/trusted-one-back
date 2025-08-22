package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.exception.BaseException;
import com.bsg.trustedone.exception.ResourceCreationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ExpertiseValidator {
    private final Validator validator;

    public void validateExpertiseCreate(ExpertiseCreationDto expertiseCreationDto) {
        doValidation(expertiseCreationDto, errors -> new ResourceCreationException("Invalid data", errors));
    }

    public void validateExpertiseUpdate(ExpertiseCreationDto expertiseCreationDto) {
        doValidation(expertiseCreationDto, errors -> new ResourceCreationException("Invalid data", errors));
    }

    private void doValidation(ExpertiseCreationDto obj, Function<List<String>, BaseException> exceptionFactory) {
        var errors = validator.validate(obj)
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        if (errors.isEmpty()) {
            return;
        }

        throw exceptionFactory.apply(errors);
    }

}

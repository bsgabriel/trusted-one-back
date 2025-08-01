package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.ProfessionCreationDto;
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
public class ProfessionValidator {
    private final Validator validator;

    public void validateProfessionCreate(ProfessionCreationDto professionCreationDto) {
        doValidation(professionCreationDto, errors -> new ResourceCreationException("Invalid data for profession creation", errors));
    }

    private void doValidation(Object obj, Function<List<String>, BaseException> exceptionFactory) {
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

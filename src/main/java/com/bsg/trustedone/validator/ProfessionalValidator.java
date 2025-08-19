package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.ProfessionalCreationDto;
import com.bsg.trustedone.exception.BaseException;
import com.bsg.trustedone.exception.ResourceCreationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfessionalValidator {

    private final Validator validator;
    private final ContactMethodValidator contactMethodValidator;

    public void validateProfessionalCreation(ProfessionalCreationDto professionalCreationDto) {
        doValidation(professionalCreationDto, errors -> new ResourceCreationException("Invalid data for professional creation", errors));
    }

    private void doValidation(ProfessionalCreationDto obj, Function<List<String>, BaseException> exceptionFactory) {
        var errors = validator.validate(obj)
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        obj.getContactMethods().forEach(c -> {
            try {
                contactMethodValidator.validateContactMethodCreation(c);
            } catch (BaseException e) {
                errors.addAll(e.getErrors());
            }
        });

        if (errors.isEmpty()) {
            return;
        }

        throw exceptionFactory.apply(errors);
    }
}

package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.CompanyCreationDto;
import com.bsg.trustedone.exception.BaseException;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.exception.ResourceUpdateException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CompanyValidator {
    private final Validator validator;

    public void validateCompanyCreate(CompanyCreationDto companyCreationDto) {
        doValidation(companyCreationDto, errors -> new ResourceCreationException("Invalid data for company creation", errors));
    }

    public void validateCompanyUpdate(CompanyCreationDto companyCreationDto) {
        doValidation(companyCreationDto, errors -> new ResourceUpdateException("Invalid data for company update", errors));
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

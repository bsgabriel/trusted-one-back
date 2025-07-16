package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.GroupCreationDto;
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
public class GroupValidator {
    private final Validator validator;

    public void validateGroupCreate(GroupCreationDto groupCreationDto) {
        doValidation(groupCreationDto, errors -> new ResourceCreationException("Invalid data for group creation", errors));
    }

    public void validateGroupUpdate(GroupCreationDto groupCreationDto) {
        doValidation(groupCreationDto, errors -> new ResourceUpdateException("Invalid data for group update", errors));
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

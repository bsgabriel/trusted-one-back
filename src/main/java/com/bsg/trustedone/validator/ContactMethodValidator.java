package com.bsg.trustedone.validator;

import com.bsg.trustedone.dto.ContactMethodCreationDto;
import com.bsg.trustedone.exception.BaseException;
import com.bsg.trustedone.exception.ResourceCreationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContactMethodValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$|^\\([0-9]{2}\\)\\s?[0-9]{4,5}-?[0-9]{4}$";
    private static final String LINKEDIN_REGEX = "^(https?://)?(www\\.)?linkedin\\.com/in/[a-zA-Z0-9-]+/?$";

    private final Validator validator;

    public void validateContactMethodCreation(ContactMethodCreationDto contactMethodCreationDto) {
        doValidation(contactMethodCreationDto, errors -> new ResourceCreationException("Invalid contact information", errors));
    }

    private void doValidation(ContactMethodCreationDto obj, Function<List<String>, BaseException> exceptionFactory) {
        var errors = validator.validate(obj)
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        errors.addAll(validateByType(obj));
        if (errors.isEmpty()) {
            return;
        }

        throw exceptionFactory.apply(errors);
    }

    private List<String> validateByType(ContactMethodCreationDto dto) {
        return switch (dto.getType()) {
            case EMAIL -> validateEmail(dto.getInfo());
            case PHONE -> validatePhone(dto.getInfo());
            case LINKEDIN -> validateLinkedIn(dto.getInfo());
            case OTHER -> Collections.emptyList();
        };
    }

    private List<String> validateEmail(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            return List.of("Invalid email format");
        }
        return Collections.emptyList();
    }

    private List<String> validatePhone(String phone) {
        if (!phone.matches(PHONE_REGEX)) {
            return List.of("Invalid phone number format");
        }
        return Collections.emptyList();
    }

    private List<String> validateLinkedIn(String linkedin) {
        if (!linkedin.matches(LINKEDIN_REGEX)) {
            return List.of("Invalid LinkedIn URL format");
        }
        return Collections.emptyList();
    }
}

package com.bsg.trustedone.validator;

import com.bsg.trustedone.annotation.ValidContactMethod;
import com.bsg.trustedone.dto.ContactMethodFormDto;
import com.bsg.trustedone.service.MessageService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactMethodValidator implements ConstraintValidator<ValidContactMethod, ContactMethodFormDto> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$|^\\([0-9]{2}\\)\\s?[0-9]{4,5}-?[0-9]{4}$";
    private static final String LINKEDIN_REGEX = "^(https?://)?(www\\.)?linkedin\\.com/in/[a-zA-Z0-9-]+/?$";

    private final MessageService messageService;

    @Override
    public boolean isValid(ContactMethodFormDto dto, ConstraintValidatorContext context) {
        // Already validated by @NotBlank and @NotNull
        if (dto == null || dto.getType() == null || dto.getInfo() == null) {
            return true;
        }

        String errorMessage = validateByType(dto);

        if (StringUtils.isNotBlank(errorMessage)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
            return false;
        }

        return true;
    }

    private String validateByType(ContactMethodFormDto dto) {
        return switch (dto.getType()) {
            case EMAIL -> validateEmail(dto.getInfo());
            case PHONE -> validatePhone(dto.getInfo());
            case LINKEDIN -> validateLinkedIn(dto.getInfo());
            case OTHER -> null;
        };
    }

    private String validateEmail(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            return messageService.getMessage("contactMethod.invalid.email", email);
        }
        return null;
    }

    private String validatePhone(String phone) {
        if (!phone.matches(PHONE_REGEX)) {
            return messageService.getMessage("contactMethod.invalid.phone", phone);
        }
        return null;
    }

    private String validateLinkedIn(String linkedIn) {
        if (!linkedIn.matches(LINKEDIN_REGEX)) {
            return messageService.getMessage("contactMethod.invalid.linkedin", linkedIn);
        }
        return null;
    }
}
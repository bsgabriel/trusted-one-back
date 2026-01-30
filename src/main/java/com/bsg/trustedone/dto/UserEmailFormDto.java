package com.bsg.trustedone.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailFormDto {

    @NotBlank(message = "{user.validation.email.required}")
    @Email(message = "{user.validation.email.invalid-format}")
    private String email;
}

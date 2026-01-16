package com.bsg.trustedone.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreationDto {

    @NotBlank(message = "{user.validation.name.required}")
    private String name;

    @NotBlank(message = "{user.validation.email.required}")
    @Email(message = "{user.validation.email.invalid-format}")
    private String email;

    @NotBlank(message = "{user.validation.password.required}")
    @Size(min = 8, message = "{user.validation.password.min-length}")
    private String password;

}

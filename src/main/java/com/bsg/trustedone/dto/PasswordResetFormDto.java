package com.bsg.trustedone.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetFormDto {

    @NotBlank(message = "{user.password-reset.validation.token.required}")
    private String token;

    @NotBlank(message = "{user.validation.password.required}")
    @Size(min = 8, message = "{user.validation.password.min-length}")
    private String password;

    @NotBlank(message = "{user.password-reset.validation.confirmPassword.required}")
    private String confirmPassword;

    @AssertTrue(message = "{user.password-reset.validation.password.match}")
    public boolean isPasswordMatch() {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            return true;
        }
        return password.equals(confirmPassword);
    }

}

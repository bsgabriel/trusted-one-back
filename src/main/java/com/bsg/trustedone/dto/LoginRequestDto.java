package com.bsg.trustedone.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {

    private String email;
    private String password;
}

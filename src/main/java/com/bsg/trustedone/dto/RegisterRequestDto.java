package com.bsg.trustedone.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestDto {

    private String name;
    private String email;
    private String password;

}

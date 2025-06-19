package com.bsg.trustedone.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Long userId;
    private String name;
    private String email;

}

package com.bsg.trustedone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {

    private Long refreshTokenId;
    private String token;
    private Instant expiryDate;
    private Long userId;

}

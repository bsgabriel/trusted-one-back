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
public class PasswordResetTokenDto {

    private Long tokenId;
    private Long userId;
    private String token;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant usedAt;
}

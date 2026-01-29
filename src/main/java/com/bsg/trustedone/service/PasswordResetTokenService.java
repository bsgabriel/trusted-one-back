package com.bsg.trustedone.service;

import com.bsg.trustedone.entity.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.password-reset.token-expiration-minutes}")
    private int tokenExpiration;

    @Transactional
    public String generateToken(Long userId) {
        var now = LocalDateTime.now();
        return this.passwordResetTokenRepository.save(PasswordResetToken.builder()
                        .userId(userId)
                        .token(UUID.randomUUID().toString())
                        .createdAt(now)
                        .expiresAt(now.plusMinutes(tokenExpiration))
                        .build())
                .getToken();
    }
}

package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.PasswordResetTokenDto;
import com.bsg.trustedone.entity.PasswordResetToken;
import com.bsg.trustedone.exception.PasswordResetException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final MessageService messageService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.password-reset.token-expiration-minutes}")
    private int tokenExpiration;

    @Transactional
    public String generateToken(Long userId) {
        passwordResetTokenRepository.deleteOldTokensForUser(userId);
        var now = Instant.now();
        return this.passwordResetTokenRepository.save(PasswordResetToken.builder()
                        .userId(userId)
                        .token(UUID.randomUUID().toString())
                        .createdAt(now)
                        .expiresAt(now.plus(tokenExpiration, ChronoUnit.MINUTES))
                        .build())
                .getToken();
    }

    public PasswordResetTokenDto findToken(String token) {
        var entity = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new PasswordResetException(
                        messageService.getMessage("error.title.operation-failed"),
                        messageService.getMessage("user.password-reset.error.invalid-token")
                ));

        if (entity.getUsedAt() != null || entity.getExpiresAt().isBefore(Instant.now())) {
            throw new PasswordResetException(
                    messageService.getMessage("error.title.operation-failed"),
                    messageService.getMessage("user.password-reset.error.invalid-token")
            );
        }

        return PasswordResetTokenDto.builder()
                .tokenId(entity.getTokenId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .usedAt(entity.getUsedAt())
                .build();
    }

    public void consumeToken(String token) {
        this.passwordResetTokenRepository.consumeToken(token, Instant.now());
    }

}

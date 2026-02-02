package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.RefreshTokenDto;
import com.bsg.trustedone.entity.RefreshToken;
import com.bsg.trustedone.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration}")
    private Long refreshTokenDuration;

    @Transactional
    public RefreshTokenDto createRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);

        var entity = refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDuration))
                .build());

        return RefreshTokenDto.builder()
                .refreshTokenId(entity.getRefreshTokenId())
                .userId(userId)
                .token(entity.getToken())
                .expiryDate(entity.getExpiryDate())
                .build();
    }

    public RefreshTokenDto findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(entity -> RefreshTokenDto.builder()
                        .refreshTokenId(entity.getRefreshTokenId())
                        .userId(entity.getUserId())
                        .token(entity.getToken())
                        .expiryDate(entity.getExpiryDate())
                        .build())
                .orElse(null);
    }

    @Transactional
    public void verifyExpiration(RefreshTokenDto token) {
        if (Instant.now().isAfter(token.getExpiryDate())) {
            refreshTokenRepository.deleteById(token.getRefreshTokenId());
            throw new RuntimeException("Refresh token expirado. Por favor, faça login novamente.");
        }
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

}
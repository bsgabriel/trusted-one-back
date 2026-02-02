package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.RefreshTokenDto;
import com.bsg.trustedone.entity.RefreshToken;
import com.bsg.trustedone.exception.SessionException;
import com.bsg.trustedone.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private MessageService messageService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDuration", 3600000L);
    }

    @Test
    @DisplayName("Should create refresh token successfully")
    void createRefreshToken_withValidUserId_shouldCreateToken() {
        var userId = 1L;

        var entity = RefreshToken.builder()
                .refreshTokenId(10L)
                .userId(userId)
                .token("token")
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepository.save(any())).thenReturn(entity);

        var result = refreshTokenService.createRefreshToken(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(entity.getToken(), result.getToken());

        verify(refreshTokenRepository).deleteByUserId(userId);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("Should find refresh token by token successfully")
    void findByToken_withValidToken_shouldReturnDto() {
        var token = "token";

        var entity = RefreshToken.builder()
                .refreshTokenId(1L)
                .userId(1L)
                .token(token)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(entity));

        var result = refreshTokenService.findByToken(token);

        assertNotNull(result);
        assertEquals(token, result.getToken());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void findByToken_withInvalidToken_shouldThrowException() {
        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThrows(SessionException.class, () -> refreshTokenService.findByToken("invalid-token"));
    }

    @Test
    @DisplayName("Should not throw exception when refresh token is not expired")
    void verifyExpiration_withValidToken_shouldNotThrowException() {
        var token = RefreshTokenDto.builder()
                .refreshTokenId(1L)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        assertDoesNotThrow(() -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should delete token and throw exception when refresh token is expired")
    void verifyExpiration_withExpiredToken_shouldDeleteAndThrowException() {
        var token = RefreshTokenDto.builder()
                .refreshTokenId(1L)
                .expiryDate(Instant.now().minusSeconds(60))
                .build();

        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThrows(SessionException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository).deleteById(token.getRefreshTokenId());
    }

    @Test
    @DisplayName("Should delete refresh token by token")
    void deleteByToken_withValidToken_shouldDelete() {
        refreshTokenService.deleteByToken("token");

        verify(refreshTokenRepository).deleteByToken("token");
    }
}

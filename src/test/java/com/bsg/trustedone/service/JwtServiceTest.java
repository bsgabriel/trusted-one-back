package com.bsg.trustedone.service;

import com.bsg.trustedone.configuration.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        when(jwtConfig.getSecret()).thenReturn("abcdefghijlkmnopqrstuvwxyz123456");
        when(jwtConfig.getExpiration()).thenReturn(3600000L);
        when(userDetails.getUsername()).thenReturn("test@email.com");
    }

    @Test
    @DisplayName("Should generate JWT token successfully")
    void generateToken_withValidUserDetails_shouldGenerateToken() {
        var token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 20);
    }

    @Test
    @DisplayName("Should extract username from token successfully")
    void extractUsername_withValidToken_shouldReturnUsername() {
        var token = jwtService.generateToken(userDetails);

        var username = jwtService.extractUsername(token);

        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    @DisplayName("Should validate token successfully")
    void validateToken_withValidToken_shouldReturnTrue() {
        var token = jwtService.generateToken(userDetails);

        var isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void extractExpiration_withValidToken_shouldReturnExpirationDate() {
        var token = jwtService.generateToken(userDetails);

        var expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }
}

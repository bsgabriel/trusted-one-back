package com.bsg.trustedone.service;

import com.bsg.trustedone.configuration.JwtConfig;
import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.dto.auth.RefreshTokenRequestDto;
import com.bsg.trustedone.entity.User;
import com.bsg.trustedone.exception.EmailException;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.mapper.UserMapper;
import com.bsg.trustedone.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MessageService messageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("Should create user successfully")
    void createUser_withValidData_shouldCreateUser() {
        var registerData = DummyObjects.newInstance(AccountCreationDto.class);
        var encodedPassword = "encodedPassword";
        var user = DummyObjects.newInstance(User.class);
        var dto = DummyObjects.newInstance(UserDto.class);

        when(userRepository.existsByEmail(registerData.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerData.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(dto);

        var result = userService.createUser(registerData);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void createUser_withExistingEmail_shouldThrowException() {
        var registerData = DummyObjects.newInstance(AccountCreationDto.class);

        when(userRepository.existsByEmail(registerData.getEmail())).thenReturn(true);
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(registerData));
    }

    @Test
    @DisplayName("Should return null when authentication is null")
    void getLoggedUser_withNoAuthentication_shouldReturnNull() {
        SecurityContextHolder.clearContext();

        var result = userService.getLoggedUser();

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when authentication is not authenticated")
    void getLoggedUser_withUnauthenticatedContext_shouldReturnNull() {
        var authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var result = userService.getLoggedUser();

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when principal is anonymous")
    void getLoggedUser_withAnonymousPrincipal_shouldReturnNull() {
        var authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var result = userService.getLoggedUser();

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when principal type is invalid")
    void getLoggedUser_withInvalidPrincipalType_shouldReturnNull() {
        var authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var result = userService.getLoggedUser();

        assertNull(result);
    }

    @Test
    @DisplayName("Should return logged user when principal is valid")
    void getLoggedUser_withValidPrincipal_shouldReturnUser() {
        var userDetail = DummyObjects.newInstance(UserDetailDto.class);
        var userDto = DummyObjects.newInstance(UserDto.class);
        var authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetail);
        when(userMapper.toUserDto(userDetail)).thenReturn(userDto);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var result = userService.getLoggedUser();

        assertNotNull(result);
        assertEquals(userDto, result);
    }

    @Test
    @DisplayName("Should login successfully")
    void login_withValidCredentials_shouldReturnTokens() {
        var request = DummyObjects.newInstance(UserLoginDto.class);
        var userDetails = DummyObjects.newInstance(UserDetailDto.class);
        var refreshToken = DummyObjects.newInstance(RefreshTokenDto.class);

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token");
        when(refreshTokenService.createRefreshToken(userDetails.getId())).thenReturn(refreshToken);
        when(jwtConfig.getExpiration()).thenReturn(3600L);

        var response = userService.login(request);

        assertNotNull(response);
        assertEquals("token", response.getAccessToken());
        assertEquals(refreshToken.getToken(), response.getRefreshToken());

        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void refreshToken_withValidToken_shouldReturnNewAccessToken() {
        var request = DummyObjects.newInstance(RefreshTokenRequestDto.class);
        var refreshToken = DummyObjects.newInstance(RefreshTokenDto.class);
        var user = DummyObjects.newInstance(User.class);
        var userDetails = DummyObjects.newInstance(UserDetailDto.class);

        when(refreshTokenService.findByToken(request.getRefreshToken())).thenReturn(refreshToken);
        doNothing().when(refreshTokenService).verifyExpiration(refreshToken);
        when(userRepository.findById(refreshToken.getUserId())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("new-token");
        when(jwtConfig.getExpiration()).thenReturn(3600L);

        var response = userService.refreshToken(request);

        assertEquals("new-token", response.getAccessToken());
        assertEquals(request.getRefreshToken(), response.getRefreshToken());
    }

    @Test
    @DisplayName("Should delete refresh token on logout")
    void logout_withValidToken_shouldDeleteToken() {
        userService.logout("refresh-token");

        verify(refreshTokenService).deleteByToken("refresh-token");
    }

    @Test
    @DisplayName("Should do nothing when refresh token is blank")
    void logout_withBlankToken_shouldDoNothing() {
        userService.logout(" ");

        verify(refreshTokenService, never()).deleteByToken(any());
    }

    @Test
    @DisplayName("Should do nothing when user email does not exist")
    void requestPasswordChange_withNonExistingEmail_shouldDoNothing() throws EmailException {
        var request = DummyObjects.newInstance(UserEmailFormDto.class);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        userService.requestPasswordChange(request);

        verify(passwordResetTokenService, never()).generateToken(anyLong());
        verify(emailService, never()).sendPasswordResetTemplate(anyString(), anyString());
    }

    @Test
    @DisplayName("Should generate token and send password reset email when user exists")
    void requestPasswordChange_withExistingUser_shouldGenerateTokenAndSendEmail() throws Exception {
        var request = DummyObjects.newInstance(UserEmailFormDto.class);
        var user = DummyObjects.newInstance(User.class);
        var token = "token";

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordResetTokenService.generateToken(user.getUserId())).thenReturn(token);

        userService.requestPasswordChange(request);

        verify(passwordResetTokenService, times(1)).generateToken(user.getUserId());
        verify(emailService, times(1)).sendPasswordResetTemplate(user.getEmail(), token);
    }

    @Test
    @DisplayName("Should not throw exception when email sending fails")
    void requestPasswordChange_whenEmailServiceFails_shouldNotThrowException() throws Exception {
        var request = DummyObjects.newInstance(UserEmailFormDto.class);
        var user = DummyObjects.newInstance(User.class);
        var token = "token";

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordResetTokenService.generateToken(user.getUserId())).thenReturn(token);

        doThrow(new RuntimeException("error"))
                .when(emailService)
                .sendPasswordResetTemplate(user.getEmail(), token);

        assertDoesNotThrow(() -> userService.requestPasswordChange(request));

        verify(passwordResetTokenService).generateToken(user.getUserId());
        verify(emailService).sendPasswordResetTemplate(user.getEmail(), token);
    }


}

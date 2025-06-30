package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.AccountCreationDto;
import com.bsg.trustedone.dto.UserDetailDto;
import com.bsg.trustedone.dto.UserLoginDto;
import com.bsg.trustedone.entity.User;
import com.bsg.trustedone.exception.AccountCreationException;
import com.bsg.trustedone.exception.UserAlreadyRegisteredException;
import com.bsg.trustedone.exception.UserLoginException;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.mapper.UserMapper;
import com.bsg.trustedone.repository.UserRepository;
import com.bsg.trustedone.validator.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpSession httpSession;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("Should create user successfully when data is valid")
    void createUser_WithValidData_ShouldCreateUserSuccessfully() {
        // Given
        var accountCreationDto = DummyObjects.newInstance(AccountCreationDto.class);
        when(userRepository.existsByEmail(accountCreationDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(accountCreationDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArguments()[0]);
        when(userMapper.toUserDto(any(User.class))).thenCallRealMethod();

        // When
        var result = userService.createUser(accountCreationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(accountCreationDto.getEmail());
        assertThat(result.getName()).isEqualTo(accountCreationDto.getName());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void createUser_WithExistingEmail_ShouldThrowUserAlreadyRegisteredException() {
        // Given
        var accountCreationDto = DummyObjects.newInstance(AccountCreationDto.class);
        when(userRepository.existsByEmail(accountCreationDto.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(accountCreationDto))
                .isInstanceOf(UserAlreadyRegisteredException.class)
                .hasMessage("Email already registered");

        verify(userValidator).validateRegistrationData(accountCreationDto);
        verify(userRepository).existsByEmail(accountCreationDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    @DisplayName("Should propagate exception when validation fails")
    void createUser_WithInvalidData_ShouldPropagateAccountCreationException() {
        // Given
        var accountCreationDto = mock(AccountCreationDto.class);
        doThrow(new AccountCreationException("Error", List.of())).when(userValidator).validateRegistrationData(accountCreationDto);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(accountCreationDto)).isInstanceOf(AccountCreationException.class);
        verify(userValidator).validateRegistrationData(accountCreationDto);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return logged user when authentication is valid")
    void getLoggedUser_WithValidAuthentication_ShouldReturnUserDto() {
        // Given
        try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            var userDetailDto = UserDetailDto.builder()
                    .id(999L)
                    .name("John Sample")
                    .email("john.sample@provider.com")
                    .build();

            var securityContext = mock(SecurityContext.class);
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetailDto);
            when(userMapper.toUserDto(userDetailDto)).thenCallRealMethod();

            // When
            var result = userService.getLoggedUser();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(userDetailDto.getName());
            assertThat(result.getEmail()).isEqualTo(userDetailDto.getEmail());
            assertThat(result.getUserId()).isEqualTo(userDetailDto.getId());
        }
    }

    @Test
    @DisplayName("Should return null when there is no authentication")
    void getLoggedUser_WithNoAuthentication_ShouldReturnNull() {
        // Given
        try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {

            var securityContext = mock(SecurityContext.class);
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // When
            var result = userService.getLoggedUser();

            // Then
            assertThat(result).isNull();
        }
    }

    @Test
    @DisplayName("Should return null when user is not authenticated")
    void getLoggedUser_WithUnauthenticatedUser_ShouldReturnNull() {
        // Given
        try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {

            var securityContext = mock(SecurityContext.class);
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            // When
            var result = userService.getLoggedUser();

            // Then
            assertThat(result).isNull();
        }
    }

    @Test
    @DisplayName("Should return null when principal is anonymousUser")
    void getLoggedUser_WithAnonymousUser_ShouldReturnNull() {
        // Given
        try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {

            var securityContext = mock(SecurityContext.class);
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("anonymousUser");

            // When
            var result = userService.getLoggedUser();

            // Then
            assertThat(result).isNull();
        }
    }

    @Test
    @DisplayName("Should return null when principal is not UserDetailDto")
    void getLoggedUser_WithInvalidPrincipalType_ShouldReturnNull() {
        // Given
        try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {

            var securityContext = mock(SecurityContext.class);
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("invalidPrincipal");

            // When
            var result = userService.getLoggedUser();

            // Then
            assertThat(result).isNull();
        }
    }

    @Test
    @DisplayName("Should login successfully when data is valid")
    void login_WithValidCredentials_ShouldAuthenticateSuccessfully() {
        try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // Given
            var loginDto = UserLoginDto.builder()
                    .email("joh.sample@provider.com")
                    .password("myPass123")
                    .build();

            var authToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
            var authenticatedToken = mock(Authentication.class);
            var securityContext = mock(SecurityContext.class);
            var httpRequest = mock(HttpServletRequest.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticatedToken);
            when(httpRequest.getSession(true)).thenReturn(httpSession);
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When
            userService.login(loginDto, httpRequest);

            // Then
            verify(userValidator).validateLoginData(loginDto);
            verify(authenticationManager).authenticate(authToken);
            verify(httpRequest).getSession(true);
            verify(httpSession).setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
        }
    }

    @Test
    @DisplayName("Should propagate exception when login validation fails")
    void login_WithInvalidLoginData_ShouldPropagateValidationException() {
        // Given
        var userLoginDto = mock(UserLoginDto.class);
        var httpRequest = mock(HttpServletRequest.class);

        doThrow(new UserLoginException("Invalid login data", List.of()))
                .when(userValidator).validateLoginData(userLoginDto);

        // When & Then
        assertThatThrownBy(() -> userService.login(userLoginDto, httpRequest))
                .isInstanceOf(UserLoginException.class)
                .hasMessage("Invalid login data");

        verify(userValidator).validateLoginData(userLoginDto);
        verify(authenticationManager, never()).authenticate(any());
        verify(httpRequest, never()).getSession(anyBoolean());
    }

    @Test
    @DisplayName("Should logout successfully when there is an active session")
    void logout_WithActiveSession_ShouldLogoutSuccessfully() {
        // Given
        try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            var httpRequest = mock(HttpServletRequest.class);
            var httpSession = mock(HttpSession.class);

            when(httpRequest.getSession(false)).thenReturn(httpSession);

            // When
            userService.logout(httpRequest);

            // Then
            verify(httpRequest).getSession(false);
            verify(httpSession).invalidate();
            mockedSecurityContextHolder.verify(SecurityContextHolder::clearContext);
        }
    }

    @Test
    @DisplayName("Should logout successfully when there is no active session")
    void logout_WithNoActiveSession_ShouldClearContextOnly() {
        // Given
        try (var mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            var httpRequest = mock(HttpServletRequest.class);
            when(httpRequest.getSession(false)).thenReturn(null);

            // When
            userService.logout(httpRequest);

            // Then
            verify(httpRequest).getSession(false);
            verify(httpSession, never()).invalidate();
            mockedSecurityContextHolder.verify(SecurityContextHolder::clearContext);
        }
    }
}
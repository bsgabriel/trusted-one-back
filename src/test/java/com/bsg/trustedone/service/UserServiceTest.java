package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.AccountCreationDto;
import com.bsg.trustedone.dto.UserDetailDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MessageService messageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

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

}

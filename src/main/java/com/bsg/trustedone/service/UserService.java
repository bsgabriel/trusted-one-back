package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.AccountCreationDto;
import com.bsg.trustedone.dto.UserDetailDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.dto.UserLoginDto;
import com.bsg.trustedone.entity.User;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.mapper.UserMapper;
import com.bsg.trustedone.repository.UserRepository;
import com.bsg.trustedone.validator.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserDto createUser(AccountCreationDto registerData) {
        userValidator.validateRegistrationData(registerData);

        if (userRepository.existsByEmail(registerData.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        var user = userRepository.save(User.builder()
                .email(registerData.getEmail())
                .name(registerData.getName())
                .password(passwordEncoder.encode(registerData.getPassword()))
                .build());

        return userMapper.toUserDto(user);
    }

    public UserDto getLoggedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication) || !authentication.isAuthenticated()) {
            return null;
        }

        var principal = authentication.getPrincipal();
        if (isNull(principal) || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        if (!(principal instanceof UserDetailDto)) {
            return null;
        }

        return userMapper.toUserDto((UserDetailDto) principal);
    }

    public void login(UserLoginDto request, HttpServletRequest httpRequest) {
        userValidator.validateLoginData(request);
        var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authToken));

        HttpSession session = httpRequest.getSession(true);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    public void logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (nonNull(session)) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}

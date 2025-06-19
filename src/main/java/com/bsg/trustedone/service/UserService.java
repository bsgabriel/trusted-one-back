package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.LoginRequestDto;
import com.bsg.trustedone.dto.RegisterRequestDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.User;
import com.bsg.trustedone.exceptions.UserAlreadyRegisteredException;
import com.bsg.trustedone.repositories.UserRepository;
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

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserDto createUser(RegisterRequestDto registerData) {
        userValidator.validateRegistrationData(registerData);

        if (userRepository.existsByEmail(registerData.getEmail())) {
            throw new UserAlreadyRegisteredException("Email already registered");
        }

        var user = userRepository.save(User.builder()
                .email(registerData.getEmail())
                .name(registerData.getName())
                .password(passwordEncoder.encode(registerData.getPassword()))
                .build());

        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public User getLoggedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication) || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public void login(LoginRequestDto request, HttpServletRequest httpRequest) {
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

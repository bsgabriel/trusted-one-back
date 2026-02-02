package com.bsg.trustedone.service;

import com.bsg.trustedone.configuration.JwtConfig;
import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.dto.auth.RefreshTokenRequestDto;
import com.bsg.trustedone.entity.User;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.SessionException;
import com.bsg.trustedone.mapper.UserMapper;
import com.bsg.trustedone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public UserDto createUser(AccountCreationDto registerData) {
        if (userRepository.existsByEmail(registerData.getEmail())) {
            throw new ResourceAlreadyExistsException(messageService.getMessage("error.title.create-resource"), messageService.getMessage("user.error.email.already-exists"));
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

    public LoginResponseDto login(UserLoginDto request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authenticationManager.authenticate(authToken);

        var userDetails = (UserDetailDto) userDetailsService.loadUserByUsername(request.getEmail());
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .type("Bearer")
                .expiresIn(jwtConfig.getExpiration())
                .build();
    }

    public LoginResponseDto refreshToken(RefreshTokenRequestDto request) {
        var token = refreshTokenService.findByToken(request.getRefreshToken());

        refreshTokenService.verifyExpiration(token);

        var userDetails = userRepository.findById(token.getUserId())
                .map(user -> userDetailsService.loadUserByUsername(user.getEmail()))
                .orElseThrow(() -> new SessionException(messageService.getMessage("session.error.invalid.title"), messageService.getMessage("session.error.invalid.message")));

        return LoginResponseDto.builder()
                .accessToken(jwtService.generateToken(userDetails))
                .refreshToken(request.getRefreshToken())
                .type("Bearer")
                .expiresIn(jwtConfig.getExpiration())
                .build();
    }

    public void logout(String refreshToken) {
        if (!StringUtils.isBlank(refreshToken)) {
            refreshTokenService.deleteByToken(refreshToken);
        }
    }

}

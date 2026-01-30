package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.dto.auth.RefreshTokenRequestDto;
import com.bsg.trustedone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid AccountCreationDto request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid UserLoginDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // TODO: remover later
        UserDto user = userService.getLoggedUser();
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody @Valid RefreshTokenRequestDto request) {
        LoginResponseDto response = userService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshTokenRequestDto request) {
        String refreshToken = request != null ? request.getRefreshToken() : null;
        userService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> requestPasswordChange(@RequestBody @Valid UserEmailFormDto request) {
        userService.requestPasswordChange(request);
        return ResponseEntity.ok().build();
    }
}

package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.LoginRequestDto;
import com.bsg.trustedone.dto.RegisterRequestDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.User;
import com.bsg.trustedone.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDto request, HttpServletRequest httpRequest) {
        userService.login(request, httpRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // TODO: remover later
        User user = userService.getLoggedUser();
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }
}

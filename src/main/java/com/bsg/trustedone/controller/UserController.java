package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.LoginRequestDto;
import com.bsg.trustedone.dto.RegisterRequestDto;
import com.bsg.trustedone.entity.User;
import com.bsg.trustedone.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        try {
            User user = userService.createUser(request.getEmail(), request.getPassword(), request.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDto request, HttpServletRequest httpRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authToken));

        HttpSession session = httpRequest.getSession(true);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout realizado com sucesso");
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

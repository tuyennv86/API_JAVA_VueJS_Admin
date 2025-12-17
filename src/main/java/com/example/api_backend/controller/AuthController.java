package com.example.api_backend.controller;

import com.example.api_backend.request.AuthRequest;
import com.example.api_backend.response.LoginResponse;
import com.example.api_backend.service.user.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/auth")

public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest.RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok(Map.of("message", "registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest.LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @RequestBody(required = false) Map<String, String> body) {
        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer ")) token = header.substring(7);
        String refresh = body != null ? body.get("refreshToken") : null;
        authService.logout(token, refresh);
        return ResponseEntity.ok(Map.of("message", "ok"));
    }
}

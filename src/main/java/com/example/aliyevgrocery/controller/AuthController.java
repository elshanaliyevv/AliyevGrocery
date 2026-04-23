package com.example.aliyevgrocery.controller;

import com.example.aliyevgrocery.model.request.RefreshTokenRequest;
import com.example.aliyevgrocery.model.request.UserLogin;
import com.example.aliyevgrocery.model.request.UserRegister;
import com.example.aliyevgrocery.service.AuthService;
import com.example.aliyevgrocery.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegister request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLogin request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(jwtService.refreshAccessToken(request.getRefreshToken()));
    }
}
package com.caiobraz.artista.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.security.AuthService;
import com.caiobraz.artista.security.dto.AuthRequest;
import com.caiobraz.artista.security.dto.AuthResponse;
import com.caiobraz.artista.security.dto.RefreshRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }
}

package com.caiobraz.artista.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.model.Usuario;
import com.caiobraz.artista.repository.UsuarioRepository;
import com.caiobraz.artista.security.dto.AuthRequest;
import com.caiobraz.artista.security.dto.AuthResponse;
import com.caiobraz.artista.service.TokenService;
import com.caiobraz.artista.service.exception.AuthException;

@RequiredArgsConstructor
@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse authenticate(AuthRequest request) {
        Usuario usuario = this.usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthException("Usuário não encontrado."));

        String jwt = this.jwtService.generateToken(usuario);
        String refresh = this.jwtService.generateRefreshToken(usuario);

        this.tokenService.criarToken(usuario, refresh);

        this.authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password(), usuario.getAuthorities())
        );

        return new AuthResponse(jwt, refresh);
    }

    public AuthResponse refreshToken(String refreshToken) {
        var username = this.jwtService.extractUsername(refreshToken);
        var user = this.usuarioRepository.findByUsername(username).orElseThrow();

        String newAccess = this.jwtService.generateToken(user);
        String newRefresh = this.jwtService.generateRefreshToken(user);

        this.tokenService.refreshToken(user, refreshToken, newRefresh);

        return new AuthResponse(newAccess, newRefresh);
    }
}

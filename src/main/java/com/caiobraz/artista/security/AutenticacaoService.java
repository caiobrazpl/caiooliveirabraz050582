package com.caiobraz.artista.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.caiobraz.artista.model.Token;
import com.caiobraz.artista.model.Usuario;
import com.caiobraz.artista.repository.TokenRepository;
import com.caiobraz.artista.repository.UsuarioRepository;
import com.caiobraz.artista.security.dto.AuthRequest;
import com.caiobraz.artista.security.dto.AuthResponse;
import com.caiobraz.artista.service.exception.AuthException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse authenticate(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username()).orElseThrow();
        String jwt = jwtService.generateToken(usuario);
        String refresh = jwtService.generateRefreshToken(usuario);

        Token token = new Token();
        token.setToken(refresh);
        token.setUsuario(usuario);
        token.setExpirado(false);
        token.setRevogado(false);
        tokenRepository.save(token);

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password(), usuario.getAuthorities())
        );

        return new AuthResponse(jwt, refresh);
    }

    public AuthResponse refreshToken(String refreshToken) {
        var username = jwtService.extractUsername(refreshToken);
        var user = usuarioRepository.findByUsername(username).orElseThrow();

        var token = tokenRepository.findByToken(refreshToken)
                .filter(t -> !t.getExpirado() && !t.getRevogado())
                .orElseThrow(() -> new AuthException("Refresh token inválido"));

        token.setExpirado(true);
        tokenRepository.save(token);

        String newAccess = jwtService.generateToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        return new AuthResponse(newAccess, newRefresh); // ou gere um novo
    }
}

package com.caiobraz.artista.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.model.Token;
import com.caiobraz.artista.model.Usuario;
import com.caiobraz.artista.repository.TokenRepository;
import com.caiobraz.artista.service.exception.AuthException;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void criarToken(Usuario usuario, String refreshToken) {
        tokenRepository.revogarTokensAntigos(usuario.getId());

        Token token = new Token();
        token.setToken(refreshToken);
        token.setUsuario(usuario);
        token.setExpirado(false);
        token.setRevogado(false);

        tokenRepository.save(token);
    }

    @Transactional
    public void refreshToken(Usuario usuario, String refreshToken, String newRefreshToken) {
        var token = tokenRepository.findByToken(refreshToken)
                .filter(t -> !t.getExpirado() && !t.getRevogado())
                .orElseThrow(() -> new AuthException("Refresh token inválido"));

        token.setExpirado(true);
        tokenRepository.save(token);

        this.criarToken(usuario, newRefreshToken);
    }
}

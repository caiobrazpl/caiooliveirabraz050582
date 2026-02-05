package com.caiobraz.artista.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    @Modifying
    @Query("UPDATE Token r " +
            "SET r.revogado = true " +
            "WHERE r.usuario.id = :idUsuario ")
    void revogarTokensAntigos(Long idUsuario);

    @Modifying
    Long deleteAllByUsuarioId(Long idUsuario);
}

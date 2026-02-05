package com.caiobraz.artista.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.model.Album;
import com.caiobraz.artista.model.enums.TipoArtista;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByIdAndAtivo(Long id, Boolean ativo);

    @Query("SELECT a FROM Album a " +
            "LEFT JOIN a.artistaAlbums aa " +
            "LEFT JOIN aa.artista ar ON ar.ativo = true " +
            "WHERE a.ativo = true " +
            "AND (:nome IS NULL OR a.nome ILIKE :nome) " +
            "AND (:tipoArtista IS NULL OR ar.tipoArtista = :tipoArtista )")
    Page<Album> listar(String nome, TipoArtista tipoArtista, Pageable pageable);
}

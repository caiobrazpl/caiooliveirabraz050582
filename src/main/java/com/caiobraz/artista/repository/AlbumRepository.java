package com.caiobraz.artista.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByIdAndAtivo(Long id, Boolean ativo);

}

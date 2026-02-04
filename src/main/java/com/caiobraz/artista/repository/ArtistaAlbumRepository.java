package com.caiobraz.artista.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.model.Album;
import com.caiobraz.artista.model.Artista;
import com.caiobraz.artista.model.ArtistaAlbum;

@Repository
public interface ArtistaAlbumRepository extends JpaRepository<ArtistaAlbum, Long> {

    Optional<ArtistaAlbum> findByArtistaAndAlbum(Artista artista, Album album);
}

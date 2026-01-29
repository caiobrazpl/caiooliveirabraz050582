package com.caiobraz.artista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.entity.ArtistaAlbum;

@Repository
public interface ArtistaAlbumRepository extends JpaRepository<ArtistaAlbum, Long> {

}

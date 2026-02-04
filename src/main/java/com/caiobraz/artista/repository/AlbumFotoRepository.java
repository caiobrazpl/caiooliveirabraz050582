package com.caiobraz.artista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.model.AlbumFoto;

@Repository
public interface AlbumFotoRepository extends JpaRepository<AlbumFoto, Long> {

}

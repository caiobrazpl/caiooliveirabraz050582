package com.caiobraz.artista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.entity.AlbumFoto;

@Repository
public interface AlbumFotoRepository extends JpaRepository<AlbumFoto, Long> {

}

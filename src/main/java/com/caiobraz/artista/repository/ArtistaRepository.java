package com.caiobraz.artista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.entity.Artista;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

}

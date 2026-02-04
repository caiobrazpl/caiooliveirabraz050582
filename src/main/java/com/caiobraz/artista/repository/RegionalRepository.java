package com.caiobraz.artista.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caiobraz.artista.model.Regional;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    Optional<Regional> findByIdExternoAndAtivoTrue(Long idExterno);

    @Modifying
    @Query("UPDATE Regional r " +
            "SET r.ativo = false " +
            "WHERE r.ativo = true " +
            "AND r.idExterno NOT IN :idsExternos ")
    Long inativarRemovidos(Set<Long> idsExternos);
}

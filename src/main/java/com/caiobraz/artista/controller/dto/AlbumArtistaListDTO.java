package com.caiobraz.artista.controller.dto;

import com.caiobraz.artista.model.Album;

public record AlbumArtistaListDTO(Long id, String nome) {

    public AlbumArtistaListDTO(Album album) {
        this(
                album.getId(),
                album.getNome()
        );
    }
}

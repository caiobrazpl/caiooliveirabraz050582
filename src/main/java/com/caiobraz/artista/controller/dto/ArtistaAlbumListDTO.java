package com.caiobraz.artista.controller.dto;

import com.caiobraz.artista.entity.Artista;

public record ArtistaAlbumListDTO(Long id, String nome) {

    public ArtistaAlbumListDTO(Artista artista) {
        this(
                artista.getId(),
                artista.getNome()
        );
    }
}

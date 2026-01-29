package com.caiobraz.artista.controller.dto;

import com.caiobraz.artista.entity.Album;

public record AlbumListDTO(Long id, String nome) {

    public AlbumListDTO(Album album) {
        this(album.getId(), album.getNome());
    }
}

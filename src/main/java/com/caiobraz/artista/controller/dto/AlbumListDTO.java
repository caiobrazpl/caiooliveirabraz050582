package com.caiobraz.artista.controller.dto;

import java.util.List;

import com.caiobraz.artista.model.Album;

public record AlbumListDTO(Long id, String nome, List<ArtistaAlbumListDTO> artistas) {

    public AlbumListDTO(Album album) {
        this(
                album.getId(),
                album.getNome(),
                album.getArtistaAlbums().stream().map(x -> new ArtistaAlbumListDTO(x.getArtista())).toList()
        );
    }
}

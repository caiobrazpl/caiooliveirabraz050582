package com.caiobraz.artista.controller.dto;

import java.util.List;

import com.caiobraz.artista.entity.Artista;

public record ArtistaListDTO(Long id, String nome, List<AlbumArtistaListDTO> albums) {

    public ArtistaListDTO(Artista artista) {
        this(
                artista.getId(),
                artista.getNome(),
                artista.getArtistaAlbums().stream().map(x -> new AlbumArtistaListDTO(x.getAlbum())).toList()
        );
    }
}

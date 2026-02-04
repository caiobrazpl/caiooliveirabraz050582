package com.caiobraz.artista.controller.dto;

import java.util.List;

import com.caiobraz.artista.model.Artista;

public record ArtistaListDTO(Long id, String nome, String tipoArtista, List<AlbumArtistaListDTO> albums) {

    public ArtistaListDTO(Artista artista) {
        this(
                artista.getId(),
                artista.getNome(),
                artista.getTipoArtista().getDescricao(),
                artista.getArtistaAlbums().stream().map(x -> new AlbumArtistaListDTO(x.getAlbum())).toList()
        );
    }
}

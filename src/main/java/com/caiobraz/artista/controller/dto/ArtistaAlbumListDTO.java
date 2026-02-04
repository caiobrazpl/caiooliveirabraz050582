package com.caiobraz.artista.controller.dto;

import com.caiobraz.artista.model.Artista;

public record ArtistaAlbumListDTO(Long id, String nome, String tipoArtista) {

    public ArtistaAlbumListDTO(Artista artista) {
        this(
                artista.getId(),
                artista.getNome(),
                artista.getTipoArtista().getDescricao()
        );
    }
}

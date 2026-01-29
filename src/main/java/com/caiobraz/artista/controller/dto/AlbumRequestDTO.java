package com.caiobraz.artista.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import com.caiobraz.artista.entity.Album;

public record AlbumRequestDTO(@NotEmpty @Size(max = 100, min = 1) String nome) {

    public Album entidade() {
        return new Album(this.nome);
    }
}

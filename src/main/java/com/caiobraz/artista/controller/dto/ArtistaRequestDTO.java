package com.caiobraz.artista.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.caiobraz.artista.model.Artista;
import com.caiobraz.artista.model.enums.TipoArtista;

public record ArtistaRequestDTO(@NotEmpty @Size(max = 100, min = 1) String nome, @NotNull TipoArtista tipoArtista) {

    public Artista entidade() {
        return new Artista(this.nome, this.tipoArtista);
    }
}

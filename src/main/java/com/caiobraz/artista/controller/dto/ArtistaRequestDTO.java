package com.caiobraz.artista.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import com.caiobraz.artista.entity.Artista;

public record ArtistaRequestDTO(@NotEmpty @Size(max = 100, min = 1) String nome) {

    public Artista entidade() {
        return new Artista(this.nome);
    }
}

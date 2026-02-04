package com.caiobraz.artista.model.enums;

import java.util.stream.Stream;

import lombok.Getter;

import com.caiobraz.artista.service.exception.BusinessException;

@Getter
public enum TipoArtista {

    CANTOR("Cantor"),
    BANDA("Banda"),;

    private final String descricao;

    TipoArtista(String descricao) {
        this.descricao = descricao;
    }

    public static TipoArtista byDescricao(String descricao) {
        return Stream.of(TipoArtista.values())
                .filter(x -> x.getDescricao().equalsIgnoreCase(descricao))
                .findFirst()
                .orElseThrow(() -> new BusinessException("artista.tipoInvalido"));
    }
}

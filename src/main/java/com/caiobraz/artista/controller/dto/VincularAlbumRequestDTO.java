package com.caiobraz.artista.controller.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record VincularAlbumRequestDTO(@NotEmpty List<Long> idsAlbums) {

}

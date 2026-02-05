package com.caiobraz.artista.security.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthRequest(@NotEmpty String login, @NotEmpty String senha) {
}

package com.caiobraz.artista.security.dto;

import jakarta.validation.constraints.NotEmpty;

public record RefreshRequest(@NotEmpty String refreshToken) {
}

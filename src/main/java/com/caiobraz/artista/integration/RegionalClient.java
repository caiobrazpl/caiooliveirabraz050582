package com.caiobraz.artista.integration;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.integration.dto.RegionalDTO;

@RequiredArgsConstructor
@Component
public class RegionalClient {

    private final WebClient webClient;

    public List<RegionalDTO> buscarRegionais() {
        return this.webClient.get()
                .uri("/regionais")
                .retrieve()
                .bodyToFlux(RegionalDTO.class)
                .collectList()
                .block();
    }
}

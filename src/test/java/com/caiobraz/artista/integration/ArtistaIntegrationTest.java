package com.caiobraz.artista.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.caiobraz.artista.controller.dto.ArtistaListDTO;
import com.caiobraz.artista.controller.dto.ArtistaRequestDTO;
import com.caiobraz.artista.controller.dto.ResponseListDTO;
import com.caiobraz.artista.model.enums.TipoArtista;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Integração - Artistas")
class ArtistaIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("GET /v1/artistas deve retornar 200 e lista paginada")
    void listarArtistasDeveRetornar200() {
        var entity = new HttpEntity<Void>(authHeaders());

        ResponseEntity<ResponseListDTO> response = restTemplate.exchange(
                "/v1/artistas?page=0&size=5",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDados().isEmpty()).isFalse();
        assertThat(response.getBody().getPaginacao()).isNotNull();
    }

    @Test
    @DisplayName("GET /v1/artistas/1 deve retornar 200 e artista")
    void buscarArtistaPorIdDeveRetornar200() {
        var entity = new HttpEntity<Void>(authHeaders());

        ResponseEntity<ArtistaListDTO> response = restTemplate.exchange(
                "/v1/artistas/1",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().nome()).isNotNull();
    }

    @Test
    @DisplayName("POST /v1/artistas deve criar artista e retornar 201")
    void criarArtistaDeveRetornar201() {
        var body = new ArtistaRequestDTO("Artista Teste Integração", TipoArtista.CANTOR);
        var entity = new HttpEntity<>(body, authHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                "/v1/artistas",
                HttpMethod.POST,
                entity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/v1/artistas/");
    }

    @Test
    @DisplayName("GET /v1/artistas/-1 deve retornar 404")
    void buscarArtistaInexistenteDeveRetornar404() {
        var entity = new HttpEntity<Void>(authHeaders());

        ResponseEntity<ArtistaListDTO> response = restTemplate.exchange(
                "/v1/artistas/-1",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET /v1/artistas sem token deve retornar 401")
    void listarSemTokenDeveRetornar401() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/artistas", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

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

import com.caiobraz.artista.controller.dto.AlbumListDTO;
import com.caiobraz.artista.controller.dto.ResponseListDTO;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Integração - Álbuns")
class AlbumIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("GET /v1/albums deve retornar 200 e lista paginada")
    void listarAlbumsDeveRetornar200() {
        var entity = new HttpEntity<Void>(authHeaders());

        ResponseEntity<ResponseListDTO> response = restTemplate.exchange(
                "/v1/albums?page=0&size=5",
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
    @DisplayName("GET /v1/albums/1 deve retornar 200 e álbum")
    void buscarAlbumPorIdDeveRetornar200() {
        var entity = new HttpEntity<Void>(authHeaders());

        ResponseEntity<AlbumListDTO> response = restTemplate.exchange(
                "/v1/albums/1",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().nome()).isNotNull();
    }
}

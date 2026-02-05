package com.caiobraz.artista.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.caiobraz.artista.security.dto.AuthRequest;
import com.caiobraz.artista.security.dto.AuthResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Integração - Autenticação")
class AutenticacaoIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("POST /v1/auth/login deve retornar 200 e tokens com credenciais válidas")
    void loginDeveRetornar200ComCredenciaisValidas() {
        var request = new AuthRequest(IntegrationTestBase.USER_ADMIN, IntegrationTestBase.PASSWORD_ADMIN);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/v1/auth/login",
                request,
                AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotBlank();
        assertThat(response.getBody().refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("POST /v1/auth/login deve retornar 401 com credenciais inválidas")
    void loginDeveRetornar401ComCredenciaisInvalidas() {
        var request = new AuthRequest("invalido", "senhaerrada");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/v1/auth/login",
                request,
                AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

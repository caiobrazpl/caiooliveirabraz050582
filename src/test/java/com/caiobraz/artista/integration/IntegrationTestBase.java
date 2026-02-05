package com.caiobraz.artista.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.caiobraz.artista.security.dto.AuthRequest;
import com.caiobraz.artista.security.dto.AuthResponse;

import static org.springframework.http.HttpStatus.OK;

/**
 * Base para testes de integração que precisam de autenticação (token JWT).
 */
@ActiveProfiles("test")
abstract class IntegrationTestBase {

    protected static final String USER_ADMIN = "admin";
    protected static final String PASSWORD_ADMIN = "admin123";

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String accessToken;

    @BeforeEach
    void login() {
        ResponseEntity<AuthResponse> auth = restTemplate.postForEntity(
                "/v1/auth/login",
                new AuthRequest(USER_ADMIN, PASSWORD_ADMIN),
                AuthResponse.class
        );
        if (auth.getStatusCode() == OK && auth.getBody() != null) {
            accessToken = auth.getBody().accessToken();
        }
    }

    protected HttpHeaders authHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null) {
            headers.setBearerAuth(accessToken);
        }
        return headers;
    }
}

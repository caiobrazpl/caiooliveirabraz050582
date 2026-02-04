package com.caiobraz.artista.config.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.health.HealthIndicator;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("minioHealth")
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;

    @Override
    public Health health() {
        try {
            // operação simples só pra validar conexão
            this.minioClient.listBuckets();

            return Health.up()
                    .withDetail("minio", "Disponível")
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("minio", "Indisponível")
                    .withException(e)
                    .build();
        }
    }
}

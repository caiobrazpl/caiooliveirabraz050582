package com.caiobraz.artista.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioClientConfig {

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.url}")
    private String url;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(this.url)
                .credentials(this.accessKey, this.secretKey)
                .build();

    }
}

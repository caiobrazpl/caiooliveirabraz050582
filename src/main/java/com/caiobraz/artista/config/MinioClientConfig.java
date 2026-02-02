package com.caiobraz.artista.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

import com.caiobraz.artista.service.exception.SystemException;

@Slf4j
@Configuration
public class MinioClientConfig {

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.url}")
    private String url;

    @Value("${minio.bucket-name}")
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        var cliente = MinioClient.builder()
                .endpoint(this.url)
                .credentials(this.accessKey, this.secretKey)
                .build();

        this.criarBucketSeNaoExiste(cliente);

        return cliente;
    }

    private void criarBucketSeNaoExiste(MinioClient minioClient) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(this.bucket).build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(this.bucket).build());
            }
        } catch (Exception ex) {
            log.error("Impossível criar bucket com nome {}", this.bucket);

            throw new SystemException("Serviço do MinIo indisponível no momento");
        }
    }
}

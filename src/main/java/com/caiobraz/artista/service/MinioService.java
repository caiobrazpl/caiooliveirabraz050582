package com.caiobraz.artista.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.caiobraz.artista.service.exception.NotFoundException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MinioService {

    @Value("${minio.bucket-name}")
    private String bucket;

    private final MinioClient minioClient;

    private static final String PASTA_FOTOS = "fotos/";

    public String enviarArquivo(MultipartFile arquivo) {
        try {
            String hash = UUID.randomUUID().toString().toUpperCase();

            var data = PutObjectArgs.builder()
                    .bucket(this.bucket)
                    .object(PASTA_FOTOS + hash)
                    .contentType(arquivo.getContentType())
                    .stream(arquivo.getInputStream(), arquivo.getSize(), -1)
                    .build();

            this.minioClient.putObject(data);
            return hash;
        } catch (Exception e) {
            log.error("Erro ao enviar Foto", e);
            return null;
        }
    }

    public void excluirArquivo(String bucket, String objectName) {
        if (StringUtils.isEmpty(objectName)) {
            throw new NotFoundException("erro.naoEncontrado");
        }

        try {
            var removeObjects = RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(PASTA_FOTOS + objectName)
                    .build();
            this.minioClient.removeObject(removeObjects);
        } catch (Exception e) {
            log.error("Erro ao excluir arquivo", e);
        }
    }

    public String buscarUrlArquivo(String bucket, String objectName) {
        try {
            var getOptions = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(PASTA_FOTOS + objectName)
                    .expiry(30, TimeUnit.MINUTES)
                    .build();

            return this.minioClient.getPresignedObjectUrl(getOptions);
        } catch (Exception e) {
            log.error("Erro ao buscar arquivo", e);
            throw new NotFoundException("minio.arquivoNaoEncontrado", new Object[]{ bucket, objectName });
        }
    }

}

package com.caiobraz.artista.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.model.Album;
import com.caiobraz.artista.model.AlbumFoto;
import com.caiobraz.artista.repository.AlbumFotoRepository;

@RequiredArgsConstructor
@Service
public class AlbumFotoService {

    private final AlbumFotoRepository albumFotoRepository;
    private final MinioService minioService;

    @Value("${minio.bucket-name}")
    private String bucket;

    public AlbumFoto criar(Album album, MultipartFile file) {
        String hash = this.minioService.enviarArquivo(file);

        var albumFoto = new AlbumFoto();
        albumFoto.setAlbum(album);
        albumFoto.setHash(hash);
        albumFoto.setDataUpload(LocalDateTime.now());
        albumFoto.setBucket(this.bucket);

        return this.albumFotoRepository.save(albumFoto);
    }

    public List<String> buscarFotosAlgum(Album album) {
        var fotos = new ArrayList<String>();
        var albumFotos = albumFotoRepository.findByAlbumId(album.getId());

        for (AlbumFoto albumFoto : albumFotos) {
            var url = minioService.buscarUrlArquivo(albumFoto.getBucket(), albumFoto.getHash());
            fotos.add(url);
        }

        return fotos;
    }
}

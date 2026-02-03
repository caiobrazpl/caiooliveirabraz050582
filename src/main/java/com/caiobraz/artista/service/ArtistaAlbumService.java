package com.caiobraz.artista.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.entity.Album;
import com.caiobraz.artista.entity.Artista;
import com.caiobraz.artista.entity.ArtistaAlbum;
import com.caiobraz.artista.repository.ArtistaAlbumRepository;

@RequiredArgsConstructor
@Service
public class ArtistaAlbumService {

    private final ArtistaAlbumRepository artistaAlbumRepository;

    public void criar(Artista artista, Album album) {
        var vinculoExistente = this.artistaAlbumRepository.findByArtistaAndAlbum(artista, album);
        if (vinculoExistente.isPresent()) {
            return;
        }

        var artistaAlbum = new ArtistaAlbum();
        artistaAlbum.setArtista(artista);
        artistaAlbum.setAlbum(album);

        this.artistaAlbumRepository.save(artistaAlbum);
    }

    public void removerVinculo(Artista artista, Album album) {
        var vinculoExistente = this.artistaAlbumRepository.findByArtistaAndAlbum(artista, album);
        if (vinculoExistente.isEmpty()) {
            return;
        }

        this.artistaAlbumRepository.delete(vinculoExistente.get());
    }
}

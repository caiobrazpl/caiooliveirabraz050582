package com.caiobraz.artista.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.controller.dto.AlbumRequestDTO;
import com.caiobraz.artista.model.Album;
import com.caiobraz.artista.model.enums.TipoArtista;
import com.caiobraz.artista.repository.AlbumRepository;
import com.caiobraz.artista.service.exception.NotFoundException;
import com.caiobraz.artista.ws.AlbumWebSocketPublisher;

@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumFotoService albumFotoService;
    private final MinioService minioService;
    private final AlbumWebSocketPublisher albumPublisher;

    public Album buscarPorId(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("erro.naoEncontrado"));
    }

    public Album buscarAtivo(Long id) {
        return albumRepository.findByIdAndAtivo(id, true)
                .orElseThrow(() -> new NotFoundException("erro.naoEncontrado"));
    }

    public Page<Album> listar(String nome, TipoArtista tipoArtista, Pageable pageable) {
        return albumRepository.listar(
                StringUtils.isEmpty(nome) ? null : "%" + nome + "%",
                tipoArtista,
                pageable
        );
    }

    @Transactional
    public Long criar(AlbumRequestDTO requestDTO) {
        var album = requestDTO.entidade();
        album.setAtivo(true);

        this.albumRepository.save(album);
        this.albumPublisher.notificarNovoAlbum(album);

        return album.getId();
    }

    @Transactional
    public void editar(Long id, AlbumRequestDTO requestDTO) {
        var album = requestDTO.entidade();

        var artistaAlterado = this.buscarPorId(id);
        artistaAlterado.setNome(album.getNome());

        this.albumRepository.save(artistaAlterado);
    }

    @Transactional
    public void excluir(Long id) {
        var artista = this.buscarPorId(id);
        artista.setAtivo(false);

        this.albumRepository.save(artista);
    }

    @Transactional
    public List<String> uploadFotos(Long idAlbum, List<MultipartFile> files) {
        var album = this.buscarPorId(idAlbum);
        var urls = new ArrayList<String>();

        for (MultipartFile file : files) {
            var fotoPessoa = this.albumFotoService.criar(album, file);
            var urlFoto = this.minioService.buscarUrlArquivo(fotoPessoa.getBucket(), fotoPessoa.getHash());
            urls.add(urlFoto);
        }

        return urls;
    }
}

package com.caiobraz.artista.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.controller.dto.ArtistaRequestDTO;
import com.caiobraz.artista.controller.dto.VincularAlbumRequestDTO;
import com.caiobraz.artista.entity.Artista;
import com.caiobraz.artista.repository.ArtistaRepository;
import com.caiobraz.artista.service.exception.NotFoundException;

import static com.caiobraz.artista.service.util.ExampleMatcherUtil.defaultMatcher;

@RequiredArgsConstructor
@Service
public class ArtistaService {

    private final ArtistaRepository artistaRepository;
    private final AlbumService albumService;
    private final ArtistaAlbumService artistaAlbumService;

    public Artista buscarPorId(Long id) {
        return artistaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("erro.naoEncontrado"));
    }

    public Artista buscarAtivo(Long id) {
        return artistaRepository.findByIdAndAtivo(id, true)
                .orElseThrow(() -> new NotFoundException("erro.naoEncontrado"));
    }

    public Page<Artista> listar(String nomeFiltro, Pageable pageable) {
        var filtro = new Artista();
        filtro.setAtivo(true);
        filtro.setNome(StringUtils.isEmpty(nomeFiltro) ? null : nomeFiltro);

        return artistaRepository.findAll(
                Example.of(filtro, defaultMatcher()),
                pageable
        );
    }

    @Transactional
    public Long criar(ArtistaRequestDTO requestDTO) {
        var artista = requestDTO.entidade();
        artista.setAtivo(true);

        this.artistaRepository.save(artista);

        return artista.getId();
    }

    @Transactional
    public void editar(Long id, ArtistaRequestDTO requestDTO) {
        var artista = requestDTO.entidade();

        var artistaAlterado = this.buscarPorId(id);
        artistaAlterado.setNome(artista.getNome());

        this.artistaRepository.save(artistaAlterado);
    }

    @Transactional
    public void excluir(Long id) {
        var artista = this.buscarPorId(id);
        artista.setAtivo(false);

        this.artistaRepository.save(artista);
    }

    @Transactional
    public void vincularAlbums(Long id, VincularAlbumRequestDTO requestDTO) {
        var artista = this.buscarAtivo(id);

        for (Long idAlbum : requestDTO.idsAlbums()) {
            var album = this.albumService.buscarAtivo(idAlbum);

            this.artistaAlbumService.criar(artista, album);
        }
    }

    @Transactional
    public void removerVinculoAlbums(Long id, VincularAlbumRequestDTO requestDTO) {
        var artista = this.buscarAtivo(id);

        for (Long idAlbum : requestDTO.idsAlbums()) {
            var album = this.albumService.buscarPorId(idAlbum);

            this.artistaAlbumService.removerVinculo(artista, album);
        }
    }
}

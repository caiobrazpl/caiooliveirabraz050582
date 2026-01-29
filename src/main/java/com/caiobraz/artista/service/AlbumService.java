package com.caiobraz.artista.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.controller.dto.AlbumRequestDTO;
import com.caiobraz.artista.entity.Album;
import com.caiobraz.artista.repository.AlbumRepository;
import com.caiobraz.artista.service.exception.NotFoundException;

import static com.caiobraz.artista.service.util.ExampleMatcherUtil.defaultMatcher;

@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public Album buscarPorId(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("erro.naoEncontrado"));
    }

    public Album buscarAtivo(Long id) {
        return albumRepository.findByIdAndAtivo(id, true)
                .orElseThrow(() -> new NotFoundException("erro.naoEncontrado"));
    }

    public Page<Album> listar(String nomeFiltro, Pageable pageable) {
        var filtro = new Album();
        filtro.setAtivo(true);
        filtro.setNome(StringUtils.isEmpty(nomeFiltro) ? null : nomeFiltro);

        return albumRepository.findAll(
                Example.of(filtro, defaultMatcher()),
                pageable
        );
    }

    public Long criar(AlbumRequestDTO requestDTO) {
        var artista = requestDTO.entidade();
        artista.setAtivo(true);

        this.albumRepository.save(artista);

        return artista.getId();
    }

    public void editar(Long id, AlbumRequestDTO requestDTO) {
        var artista = requestDTO.entidade();

        var artistaAlterado = this.buscarPorId(id);
        artistaAlterado.setNome(artista.getNome());

        this.albumRepository.save(artistaAlterado);
    }

    public void excluir(Long id) {
        var artista = this.buscarPorId(id);
        artista.setAtivo(false);

        this.albumRepository.save(artista);
    }
}

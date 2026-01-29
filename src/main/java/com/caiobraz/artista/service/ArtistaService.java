package com.caiobraz.artista.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.controller.dto.ArtistaRequestDTO;
import com.caiobraz.artista.entity.Artista;
import com.caiobraz.artista.repository.ArtistaRepository;
import com.caiobraz.artista.service.exception.NotFoundException;

import static com.caiobraz.artista.service.util.ExampleMatcherUtil.defaultMatcher;

@RequiredArgsConstructor
@Service
public class ArtistaService {

    private final ArtistaRepository artistaRepository;

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

    public Long criar(ArtistaRequestDTO requestDTO) {
        var artista = requestDTO.entidade();
        artista.setAtivo(true);

        this.artistaRepository.save(artista);

        return artista.getId();
    }

    public void editar(Long id, ArtistaRequestDTO requestDTO) {
        var artista = requestDTO.entidade();

        var artistaAlterado = this.buscarPorId(id);
        artistaAlterado.setNome(artista.getNome());

        this.artistaRepository.save(artistaAlterado);
    }

    public void excluir(Long id) {
        var artista = this.buscarPorId(id);
        artista.setAtivo(false);

        this.artistaRepository.save(artista);
    }
}

package com.caiobraz.artista.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.entity.Artista;
import com.caiobraz.artista.repository.ArtistaRepository;
import com.caiobraz.artista.service.exception.NotFoundException;

import static com.caiobraz.artista.service.util.ExampleMatcherUtil.defaultMatcher;

@RequiredArgsConstructor
@Service
public class ArtistaService {

    private final ArtistaRepository artistaRepository;

    public Artista buscar(Long id) {
        return artistaRepository.findById(id)
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
}

package com.caiobraz.artista.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.controller.dto.ArtistaListDTO;
import com.caiobraz.artista.controller.dto.Paginacao;
import com.caiobraz.artista.controller.dto.ResponseListDTO;
import com.caiobraz.artista.service.ArtistaService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ArtistaListDTO> buscar(@PathVariable Long id) {
        var registro = this.artistaService.buscar(id);
        var response = new ArtistaListDTO(registro);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<ResponseListDTO<ArtistaListDTO>> listar(
            @RequestParam(required = false) String nome, Pageable pageable) {

        var list = artistaService.listar(nome, pageable);
        var response = list.stream().map(ArtistaListDTO::new).toList();

        return ResponseEntity.ok(new ResponseListDTO<>(response, new Paginacao(list)));
    }

}

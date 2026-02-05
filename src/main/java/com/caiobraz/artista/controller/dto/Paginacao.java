package com.caiobraz.artista.controller.dto;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class Paginacao {

    private final int pagina;
    private final int totalPaginas;
    private final int registrosPorPagina;
    private final long totalRegistros;

    public Paginacao() {
        this.pagina = 0;
        this.totalPaginas = 0;
        this.registrosPorPagina = 0;
        this.totalRegistros = 0;
    }

    public Paginacao(Page page) {
        this.pagina = page.getPageable().getPageNumber();
        this.totalPaginas = page.getTotalPages();
        this.registrosPorPagina = page.getPageable().getPageSize();
        this.totalRegistros = page.getTotalElements();
    }

}

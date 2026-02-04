package com.caiobraz.artista.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.caiobraz.artista.client.RegionalClient;
import com.caiobraz.artista.client.dto.RegionalDTO;
import com.caiobraz.artista.model.Regional;
import com.caiobraz.artista.repository.RegionalRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegionalService {

    private final RegionalRepository repository;
    private final RegionalClient regionalClient;

    @Transactional
    public void sincronizar() {
        var regionaisExternas = this.regionalClient.buscarRegionais();
        log.info("Regionais externas encontradas: {}", regionaisExternas.size());

        for (var regionalDTO : regionaisExternas) {
            this.sincronizarRegional(regionalDTO);
        }

        this.inativarRemovidos(regionaisExternas);
    }

    private void sincronizarRegional(RegionalDTO regionalDTO) {
        var optInterno = this.repository.findByIdExternoAndAtivoTrue(regionalDTO.getId());

        if (optInterno.isEmpty()) {
            log.info("Regional interna não encontrada. Uma nova será inserida: {} - {}",
                    regionalDTO.getId(), regionalDTO.getNome());
            this.salvarNovo(regionalDTO);
            return;
        }

        var interno = optInterno.get();

        if (interno.getNome().equals(regionalDTO.getNome().trim())) {
            log.info("Regional interna encontrada e idêntica. {} - {}",
                    regionalDTO.getId(), regionalDTO.getNome());
            interno.setAtivo(true);
            this.repository.save(interno);
            return;
        }

        log.info("Regional externa modificada. A interna inativada e uma nova será inserida. {} - {}",
                regionalDTO.getId(), regionalDTO.getNome());
        interno.setAtivo(false);
        this.repository.save(interno);
        this.salvarNovo(regionalDTO);
    }

    private void salvarNovo(RegionalDTO dto) {
        var novo = new Regional();

        novo.setIdExterno(dto.getId());
        novo.setNome(dto.getNome().trim());
        novo.setAtivo(true);

        repository.save(novo);
    }

    private void inativarRemovidos(List<RegionalDTO> regionalExternas) {
        var idsExternos = regionalExternas.stream()
                .map(RegionalDTO::getId)
                .collect(Collectors.toSet());

        var removidos = this.repository.inativarRemovidos(idsExternos);
        log.info("Removidos inativados com sucesso: {}", removidos);
    }
}

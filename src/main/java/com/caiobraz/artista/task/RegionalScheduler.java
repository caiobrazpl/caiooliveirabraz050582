package com.caiobraz.artista.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.caiobraz.artista.service.RegionalService;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegionalScheduler {

    private final RegionalService regionalService;

    @Scheduled(cron = "0 0 * * * *")// a cada hora
    public void executarSync() {
        try {
            log.info("Iniciando sincronização de regionais...");

            regionalService.sincronizar();

            log.info("Sincronização de regionais finalizada.");
        } catch (Exception e) {
            log.error("Erro ao sincronizar regionais", e);
        }
    }
}

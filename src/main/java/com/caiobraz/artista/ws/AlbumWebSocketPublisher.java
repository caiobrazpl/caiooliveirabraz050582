package com.caiobraz.artista.ws;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.model.Album;
import com.caiobraz.artista.ws.dto.AlbumNotificacaoDTO;

@RequiredArgsConstructor
@Service
public class AlbumWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void notificarNovoAlbum(Album album) {
        this.messagingTemplate.convertAndSend(
                "/topic/albums",
                new AlbumNotificacaoDTO(album.getId(), album.getNome())
        );
    }
}

package com.caiobraz.artista.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caiobraz.artista.model.Album;
import com.caiobraz.artista.model.Artista;
import com.caiobraz.artista.model.ArtistaAlbum;
import com.caiobraz.artista.model.enums.TipoArtista;
import com.caiobraz.artista.repository.ArtistaAlbumRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistaAlbumService")
class ArtistaAlbumServiceTest {

    @Mock
    private ArtistaAlbumRepository artistaAlbumRepository;

    @InjectMocks
    private ArtistaAlbumService artistaAlbumService;

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("deve criar vínculo quando não existir")
        void deveCriarVinculoQuandoNaoExistir() {
            var artista = new Artista("Artista", TipoArtista.CANTOR);
            artista.setId(1L);
            var album = new Album("Álbum");
            album.setId(2L);
            when(artistaAlbumRepository.findByArtistaAndAlbum(artista, album)).thenReturn(Optional.empty());
            when(artistaAlbumRepository.save(any(ArtistaAlbum.class))).thenAnswer(inv -> inv.getArgument(0));

            artistaAlbumService.criar(artista, album);

            verify(artistaAlbumRepository).save(any(ArtistaAlbum.class));
        }

        @Test
        @DisplayName("não deve criar vínculo quando já existir (idempotente)")
        void naoDeveCriarVinculoQuandoJaExistir() {
            var artista = new Artista("Artista", TipoArtista.CANTOR);
            artista.setId(1L);
            var album = new Album("Álbum");
            album.setId(2L);
            var vinculoExistente = new ArtistaAlbum();
            when(artistaAlbumRepository.findByArtistaAndAlbum(artista, album)).thenReturn(Optional.of(vinculoExistente));

            artistaAlbumService.criar(artista, album);

            verify(artistaAlbumRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removerVinculo")
    class RemoverVinculo {

        @Test
        @DisplayName("deve remover vínculo quando existir")
        void deveRemoverVinculoQuandoExistir() {
            var artista = new Artista("Artista", TipoArtista.CANTOR);
            artista.setId(1L);
            var album = new Album("Álbum");
            album.setId(2L);
            var vinculo = new ArtistaAlbum();
            when(artistaAlbumRepository.findByArtistaAndAlbum(artista, album)).thenReturn(Optional.of(vinculo));

            artistaAlbumService.removerVinculo(artista, album);

            verify(artistaAlbumRepository).delete(vinculo);
        }

        @Test
        @DisplayName("não deve lançar exceção quando vínculo não existir")
        void naoDeveLancarExcecaoQuandoVinculoNaoExistir() {
            var artista = new Artista("Artista", TipoArtista.CANTOR);
            artista.setId(1L);
            var album = new Album("Álbum");
            album.setId(2L);
            when(artistaAlbumRepository.findByArtistaAndAlbum(artista, album)).thenReturn(Optional.empty());

            artistaAlbumService.removerVinculo(artista, album);

            verify(artistaAlbumRepository, never()).delete(any());
        }
    }
}

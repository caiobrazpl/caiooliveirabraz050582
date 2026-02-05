package com.caiobraz.artista.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.caiobraz.artista.controller.dto.ArtistaRequestDTO;
import com.caiobraz.artista.controller.dto.VincularAlbumRequestDTO;
import com.caiobraz.artista.model.Album;
import com.caiobraz.artista.model.Artista;
import com.caiobraz.artista.model.enums.TipoArtista;
import com.caiobraz.artista.repository.ArtistaRepository;
import com.caiobraz.artista.service.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistaService")
class ArtistaServiceTest {

    @Mock
    private ArtistaRepository artistaRepository;

    @Mock
    private AlbumService albumService;

    @Mock
    private ArtistaAlbumService artistaAlbumService;

    @InjectMocks
    private ArtistaService artistaService;

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar artista quando existir")
        void deveRetornarArtistaQuandoExistir() {
            var artista = new Artista("Caetano Veloso", TipoArtista.CANTOR);
            artista.setId(1L);
            when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));

            var resultado = artistaService.buscarPorId(1L);

            assertThat(resultado).isNotNull().isEqualTo(artista);
            assertThat(resultado.getNome()).isEqualTo("Caetano Veloso");
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando não existir")
        void deveLancarNotFoundExceptionQuandoNaoExistir() {
            when(artistaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> artistaService.buscarPorId(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("erro.naoEncontrado");
        }
    }

    @Nested
    @DisplayName("buscarAtivo")
    class BuscarAtivo {

        @Test
        @DisplayName("deve retornar artista ativo quando existir")
        void deveRetornarArtistaAtivoQuandoExistir() {
            var artista = new Artista("Gilberto Gil", TipoArtista.CANTOR);
            artista.setId(2L);
            artista.setAtivo(true);
            when(artistaRepository.findByIdAndAtivo(2L, true)).thenReturn(Optional.of(artista));

            var resultado = artistaService.buscarAtivo(2L);

            assertThat(resultado).isNotNull().isEqualTo(artista);
            assertThat(resultado.getAtivo()).isTrue();
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando não existir ativo")
        void deveLancarNotFoundExceptionQuandoNaoExistirAtivo() {
            when(artistaRepository.findByIdAndAtivo(999L, true)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> artistaService.buscarAtivo(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("erro.naoEncontrado");
        }
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("deve retornar página de artistas")
        void deveRetornarPaginaDeArtistas() {
            var artista = new Artista("Rita Lee", TipoArtista.CANTOR);
            artista.setId(3L);
            artista.setAtivo(true);
            Pageable pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(artista), pageable, 1);
            when(artistaRepository.findAll(any(), eq(pageable))).thenReturn(page);

            var resultado = artistaService.listar(null, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Rita Lee");
        }

        @Test
        @DisplayName("deve filtrar por nome quando informado")
        void deveFiltrarPorNomeQuandoInformado() {
            Pageable pageable = PageRequest.of(0, 10);
            when(artistaRepository.findAll(any(), eq(pageable))).thenReturn(Page.empty(pageable));

            artistaService.listar("Caetano", pageable);

            verify(artistaRepository).findAll(any(), eq(pageable));
        }
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("deve criar artista e retornar id")
        void deveCriarArtistaERetornarId() {
            var request = new ArtistaRequestDTO("Novo Artista", TipoArtista.BANDA);
            when(artistaRepository.save(any(Artista.class))).thenAnswer(inv -> {
                Artista a = inv.getArgument(0);
                a.setId(10L);
                return a;
            });

            var id = artistaService.criar(request);

            assertThat(id).isEqualTo(10L);
            verify(artistaRepository).save(any(Artista.class));
        }
    }

    @Nested
    @DisplayName("editar")
    class Editar {

        @Test
        @DisplayName("deve atualizar nome do artista")
        void deveAtualizarNomeDoArtista() {
            var artistaExistente = new Artista("Nome Antigo", TipoArtista.CANTOR);
            artistaExistente.setId(1L);
            when(artistaRepository.findById(1L)).thenReturn(Optional.of(artistaExistente));
            when(artistaRepository.save(any(Artista.class))).thenAnswer(inv -> inv.getArgument(0));

            var request = new ArtistaRequestDTO("Nome Novo", TipoArtista.CANTOR);
            artistaService.editar(1L, request);

            assertThat(artistaExistente.getNome()).isEqualTo("Nome Novo");
            verify(artistaRepository).save(artistaExistente);
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando artista não existir")
        void deveLancarNotFoundExceptionQuandoArtistaNaoExistir() {
            when(artistaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> artistaService.editar(999L, new ArtistaRequestDTO("Nome", TipoArtista.CANTOR)))
                    .isInstanceOf(NotFoundException.class);
            verify(artistaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("excluir")
    class Excluir {

        @Test
        @DisplayName("deve fazer exclusão lógica (ativo = false)")
        void deveFazerExclusaoLogica() {
            var artista = new Artista("Artista", TipoArtista.CANTOR);
            artista.setId(1L);
            artista.setAtivo(true);
            when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));
            when(artistaRepository.save(any(Artista.class))).thenAnswer(inv -> inv.getArgument(0));

            artistaService.excluir(1L);

            assertThat(artista.getAtivo()).isFalse();
            verify(artistaRepository).save(artista);
        }
    }

    @Nested
    @DisplayName("vincularAlbums")
    class VincularAlbums {

        @Test
        @DisplayName("deve vincular álbuns ao artista")
        void deveVincularAlbumsAoArtista() {
            var artista = new Artista("Artista", TipoArtista.CANTOR);
            artista.setId(1L);
            artista.setAtivo(true);
            var album = new Album("Álbum");
            album.setId(5L);
            when(artistaRepository.findByIdAndAtivo(1L, true)).thenReturn(Optional.of(artista));
            when(albumService.buscarAtivo(5L)).thenReturn(album);

            artistaService.vincularAlbums(1L, new VincularAlbumRequestDTO(List.of(5L)));

            verify(artistaAlbumService).criar(artista, album);
        }
    }

    @Nested
    @DisplayName("removerVinculoAlbums")
    class RemoverVinculoAlbums {

        @Test
        @DisplayName("deve remover vínculo de álbuns")
        void deveRemoverVinculoDeAlbums() {
            var artista = new Artista("Artista", TipoArtista.CANTOR);
            artista.setId(1L);
            artista.setAtivo(true);
            var album = new Album("Álbum");
            album.setId(5L);
            when(artistaRepository.findByIdAndAtivo(1L, true)).thenReturn(Optional.of(artista));
            when(albumService.buscarPorId(5L)).thenReturn(album);

            artistaService.removerVinculoAlbums(1L, new VincularAlbumRequestDTO(List.of(5L)));

            verify(artistaAlbumService).removerVinculo(artista, album);
        }
    }
}

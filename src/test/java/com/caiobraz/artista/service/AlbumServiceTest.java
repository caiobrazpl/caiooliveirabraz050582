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
import org.springframework.web.multipart.MultipartFile;

import com.caiobraz.artista.controller.dto.AlbumRequestDTO;
import com.caiobraz.artista.model.Album;
import com.caiobraz.artista.model.AlbumFoto;
import com.caiobraz.artista.model.enums.TipoArtista;
import com.caiobraz.artista.repository.AlbumRepository;
import com.caiobraz.artista.service.exception.NotFoundException;
import com.caiobraz.artista.ws.AlbumWebSocketPublisher;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlbumService")
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private AlbumFotoService albumFotoService;

    @Mock
    private MinioService minioService;

    @Mock
    private AlbumWebSocketPublisher albumPublisher;

    @InjectMocks
    private AlbumService albumService;

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar álbum quando existir")
        void deveRetornarAlbumQuandoExistir() {
            var album = new Album("Acústico MTV");
            album.setId(1L);
            when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

            var resultado = albumService.buscarPorId(1L);

            assertThat(resultado).isNotNull().isEqualTo(album);
            assertThat(resultado.getNome()).isEqualTo("Acústico MTV");
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando não existir")
        void deveLancarNotFoundExceptionQuandoNaoExistir() {
            when(albumRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> albumService.buscarPorId(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("erro.naoEncontrado");
        }
    }

    @Nested
    @DisplayName("buscarAtivo")
    class BuscarAtivo {

        @Test
        @DisplayName("deve retornar álbum ativo quando existir")
        void deveRetornarAlbumAtivoQuandoExistir() {
            var album = new Album("Álbum Ativo");
            album.setId(2L);
            album.setAtivo(true);
            when(albumRepository.findByIdAndAtivo(2L, true)).thenReturn(Optional.of(album));

            var resultado = albumService.buscarAtivo(2L);

            assertThat(resultado).isNotNull().isEqualTo(album);
            assertThat(resultado.getAtivo()).isTrue();
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando não existir ativo")
        void deveLancarNotFoundExceptionQuandoNaoExistirAtivo() {
            when(albumRepository.findByIdAndAtivo(999L, true)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> albumService.buscarAtivo(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("erro.naoEncontrado");
        }
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("deve retornar página de álbuns")
        void deveRetornarPaginaDeAlbums() {
            var album = new Album("Guns");
            album.setId(3L);
            Pageable pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(album), pageable, 1);
            when(albumRepository.listar(eq("%Guns%"), eq(TipoArtista.BANDA), eq(pageable))).thenReturn(page);

            var resultado = albumService.listar("Guns", TipoArtista.BANDA, pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().getFirst().getNome()).isEqualTo("Guns");
        }

        @Test
        @DisplayName("deve aceitar nome e tipo nulos")
        void deveAceitarNomeETipoNulos() {
            Pageable pageable = PageRequest.of(0, 10);
            when(albumRepository.listar(eq(null), eq(null), eq(pageable))).thenReturn(Page.empty(pageable));

            albumService.listar(null, null, pageable);

            verify(albumRepository).listar(null, null, pageable);
        }
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("deve criar álbum, notificar e retornar id")
        void deveCriarAlbumNotificarERetornarId() {
            var request = new AlbumRequestDTO("Novo Álbum");
            when(albumRepository.save(any(Album.class))).thenAnswer(inv -> {
                Album a = inv.getArgument(0);
                a.setId(10L);
                return a;
            });

            var id = albumService.criar(request);

            assertThat(id).isEqualTo(10L);
            verify(albumRepository).save(any(Album.class));
            verify(albumPublisher).notificarNovoAlbum(any(Album.class));
        }
    }

    @Nested
    @DisplayName("editar")
    class Editar {

        @Test
        @DisplayName("deve atualizar nome do álbum")
        void deveAtualizarNomeDoAlbum() {
            var albumExistente = new Album("Nome Antigo");
            albumExistente.setId(1L);
            when(albumRepository.findById(1L)).thenReturn(Optional.of(albumExistente));
            when(albumRepository.save(any(Album.class))).thenAnswer(inv -> inv.getArgument(0));

            var request = new AlbumRequestDTO("Nome Novo");
            albumService.editar(1L, request);

            assertThat(albumExistente.getNome()).isEqualTo("Nome Novo");
            verify(albumRepository).save(albumExistente);
        }

        @Test
        @DisplayName("deve lançar NotFoundException quando álbum não existir")
        void deveLancarNotFoundExceptionQuandoAlbumNaoExistir() {
            when(albumRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> albumService.editar(999L, new AlbumRequestDTO("Nome")))
                    .isInstanceOf(NotFoundException.class);
            verify(albumRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("excluir")
    class Excluir {

        @Test
        @DisplayName("deve fazer exclusão lógica (ativo = false)")
        void deveFazerExclusaoLogica() {
            var album = new Album("Álbum");
            album.setId(1L);
            album.setAtivo(true);
            when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
            when(albumRepository.save(any(Album.class))).thenAnswer(inv -> inv.getArgument(0));

            albumService.excluir(1L);

            assertThat(album.getAtivo()).isFalse();
            verify(albumRepository).save(album);
        }
    }

    @Nested
    @DisplayName("uploadFotos")
    class UploadFotos {

        @Test
        @DisplayName("deve criar fotos e retornar urls")
        void deveCriarFotosERetornarUrls() {
            var album = new Album("Álbum");
            album.setId(1L);
            var foto = new AlbumFoto();
            foto.setBucket("bucket");
            foto.setHash("hash1");
            var multipart = org.mockito.Mockito.mock(MultipartFile.class);
            when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
            when(albumFotoService.criar(eq(album), any(MultipartFile.class))).thenReturn(foto);
            when(minioService.buscarUrlArquivo("bucket", "hash1")).thenReturn("http://minio/url1");

            var urls = albumService.uploadFotos(1L, List.of(multipart));

            assertThat(urls).hasSize(1).containsExactly("http://minio/url1");
            verify(albumFotoService).criar(album, multipart);
            verify(minioService).buscarUrlArquivo("bucket", "hash1");
        }
    }
}

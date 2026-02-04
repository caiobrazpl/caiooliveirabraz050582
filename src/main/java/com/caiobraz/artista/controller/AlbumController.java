package com.caiobraz.artista.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.controller.dto.AlbumListDTO;
import com.caiobraz.artista.controller.dto.AlbumRequestDTO;
import com.caiobraz.artista.controller.dto.Paginacao;
import com.caiobraz.artista.controller.dto.ResponseListDTO;
import com.caiobraz.artista.model.enums.TipoArtista;
import com.caiobraz.artista.service.AlbumService;


@Tag(name = "Álbuns", description = "Endpoints para gestão de álbuns: consulta, listagem paginada, criação, edição, exclusão e upload de fotos.")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/${app.api.version}/albums")
public class AlbumController {

    private final AlbumService albumService;

    @Operation(summary = "Buscar álbum por ID", description = "Retorna um álbum ativo pelo identificador.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Álbum encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AlbumListDTO.class))),
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AlbumListDTO> buscar(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "ID do álbum",
                    example = "10",
                    required = true
            )
            @PathVariable Long id) {
        var registro = this.albumService.buscarAtivo(id);

        return ResponseEntity.ok(new AlbumListDTO(registro));
    }

    @Operation(summary = "Listar álbuns (paginado)",
            description = """
            Lista álbuns ativos com paginação e ordenação.
            Permite filtrar por nome (opcional) e por tipo de artista (opcional).
            
            Exemplo de paginação: ?page=0&size=10&sort=nome,asc
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseListDTO.class))),
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<ResponseListDTO<AlbumListDTO>> listar(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Filtro por nome do álbum (opcional). Pode ser parcial.",
                    example = "Guns"
            )
            @RequestParam(required = false) String nome,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Filtro por tipo do artista relacionado (opcional). Ex.: CANTOR/BANDA",
                    example = "BANDA"
            )
            @RequestParam(required = false) TipoArtista tipoArtista,
            @ParameterObject Pageable pageable) {

        var list = albumService.listar(nome, tipoArtista, pageable);
        var response = list.stream().map(AlbumListDTO::new).toList();

        return ResponseEntity.ok(new ResponseListDTO<>(response, new Paginacao(list)));
    }

    @Operation(summary = "Criar álbum", description = "Cria um novo álbum. Retorna **201 Created** e o header **Location** apontando para o recurso criado.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Álbum criado com sucesso")})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Void> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dados para criação do álbum",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AlbumRequestDTO.class),
                            examples = @ExampleObject(value = "{\"nome\": \"Acústico MTV\"}") ))
            @Valid @RequestBody AlbumRequestDTO requestDTO) {
        var id = this.albumService.criar(requestDTO);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Editar álbum", description = "Atualiza os dados do álbum identificado por ID.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Álbum atualizado com sucesso")})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(
            @Parameter(in = ParameterIn.PATH, description = "ID do álbum", example = "10", required = true)
            @PathVariable Long id, @Valid @RequestBody AlbumRequestDTO requestDTO) {
        this.albumService.editar(id, requestDTO);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Excluir álbum", description = "Remove de forma lógica o álbum identificado por ID.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Álbum excluído com sucesso")})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(in = ParameterIn.PATH, description = "ID do álbum", example = "10", required = true)
            @PathVariable Long id) {
        this.albumService.excluir(id);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Upload de fotos do álbum",
            description = """
            Faz upload de 1 ou mais fotos para o álbum.
            Retorna uma lista de URLs das imagens armazenadas.
            
            - Content-Type: multipart/form-data
            - Campo: arquivos (pode enviar múltiplos)
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Upload realizado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = @ExampleObject(value = "['http://minio/albums/10/foto1.jpg', 'http://minio/albums/10/foto2.jpg']")
                    )
            ),
            @ApiResponse(responseCode = "413", description = "Arquivo(s) muito grande(s) (Payload Too Large)"),
            @ApiResponse(responseCode = "415", description = "Tipo de mídia não suportado"),
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/fotos")
    public ResponseEntity<List<String>> uploadFoto(
            @Parameter(in = ParameterIn.PATH, description = "ID do álbum", example = "10", required = true)
            @PathVariable Long id,
            @Parameter(
                    in = ParameterIn.DEFAULT,
                    description = "Arquivos de imagem (pode enviar múltiplos). Campo multipart: **arquivos**",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"),
                            encoding = @Encoding(name = "arquivos", contentType = "image/*")
                    )
            )
            @RequestParam("arquivos") List<MultipartFile> arquivos) {

        var url = this.albumService.uploadFotos(id, arquivos);

        return ResponseEntity.ok(url);
    }
}

package com.caiobraz.artista.controller;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.caiobraz.artista.controller.dto.ArtistaListDTO;
import com.caiobraz.artista.controller.dto.ArtistaRequestDTO;
import com.caiobraz.artista.controller.dto.Paginacao;
import com.caiobraz.artista.controller.dto.ResponseListDTO;
import com.caiobraz.artista.controller.dto.VincularAlbumRequestDTO;
import com.caiobraz.artista.service.ArtistaService;


@Tag(name = "Artistas", description = "Endpoints para gestão de artistas: consulta, listagem paginada, criação, edição, exclusão e vínculo de álbuns.")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/${app.api.version}/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    @Operation(summary = "Buscar artista por ID", description = "Retorna um artista ativo pelo identificador.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Artista encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ArtistaListDTO.class))),
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ArtistaListDTO> buscar(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "ID do artista",
                    example = "10",
                    required = true
            )
            @PathVariable Long id
    ) {
        var registro = this.artistaService.buscarAtivo(id);
        var response = new ArtistaListDTO(registro);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar artistas (paginado)", description = "Lista artistas ativos com paginação e ordenação. Pode filtrar por nome (opcional).")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseListDTO.class))),
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<ResponseListDTO<ArtistaListDTO>> listar(
            @Parameter(in = ParameterIn.QUERY,
                    description = "Filtro por nome (contém/like). Se não informado, retorna todos.",
                    example = "Caetano"
            )
            @RequestParam(required = false) String nome,
            @ParameterObject Pageable pageable
    ) {
        var list = artistaService.listar(nome, pageable);
        var response = list.stream().map(ArtistaListDTO::new).toList();
        return ResponseEntity.ok(new ResponseListDTO<>(response, new Paginacao(list)));
    }

    @Operation(summary = "Criar artista", description = "Cria um novo artista. Retorna **201 Created** e o header **Location** apontando para o recurso criado.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Artista criado com sucesso")})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Void> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Dados para criação do artista",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ArtistaRequestDTO.class),
                            examples = @ExampleObject(value = "{\"nome\": \"Caetano Veloso\"}")
                    )
            )
            @Valid @RequestBody ArtistaRequestDTO requestDTO
    ) {
        var id = this.artistaService.criar(requestDTO);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Editar artista", description = "Atualiza os dados do artista identificado por ID.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Artista atualizado com sucesso")})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(
            @Parameter(in = ParameterIn.PATH, description = "ID do artista", example = "10", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Dados para alteração do artista",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ArtistaRequestDTO.class),
                            examples = @ExampleObject(value = "{\"nome\": \"Caetano Veloso\"}")
                    )
            )
            @Valid @RequestBody ArtistaRequestDTO requestDTO
    ) {
        this.artistaService.editar(id, requestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Excluir artista", description = "Realiza exclusão lógica (ou física, conforme regra do serviço) do artista.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Artista excluído com sucesso")})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(in = ParameterIn.PATH, description = "ID do artista", example = "10", required = true)
            @PathVariable Long id
    ) {
        this.artistaService.excluir(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Vincular álbuns ao artista", description = "Vincula uma lista de álbuns a um artista. A operação deve ser idempotente (se já estiver vinculado, ignora).")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Vínculo realizado com sucesso")})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/albums")
    public ResponseEntity<Void> vincularAlbums(
            @Parameter(in = ParameterIn.PATH, description = "ID do artista", example = "10", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "IDs dos álbuns que serão vinculados ao artista.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VincularAlbumRequestDTO.class),
                            examples = @ExampleObject(value = "{\"idsAlbums\": [5, 6, 8]}")
                    )
            )
            @Valid @RequestBody VincularAlbumRequestDTO requestDTO
    ) {
        this.artistaService.vincularAlbums(id, requestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remover vínculo de álbuns do artista", description = "Remove o vínculo entre o artista e uma lista de álbuns.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Vínculo removido com sucesso"),})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}/albums")
    public ResponseEntity<Void> removerVinculoAlbums(
            @Parameter(in = ParameterIn.PATH, description = "ID do artista", example = "10", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "IDs dos álbuns que serão desvinculados ao artista.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VincularAlbumRequestDTO.class),
                            examples = @ExampleObject(value = "{\"idsAlbums\": [5, 6, 8]}")
                    )
            )
            @Valid @RequestBody VincularAlbumRequestDTO requestDTO
    ) {
        this.artistaService.removerVinculoAlbums(id, requestDTO);
        return ResponseEntity.ok().build();
    }

}

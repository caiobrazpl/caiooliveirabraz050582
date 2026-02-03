package com.caiobraz.artista.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.caiobraz.artista.entity.enums.TipoArtista;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ARTISTA")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "TIPO")
    private TipoArtista tipoArtista;

    @Column(name = "ATIVO")
    private Boolean ativo;

    @OneToMany(mappedBy = "artista")
    private List<ArtistaAlbum> artistaAlbums;

    public Artista(String nome) {
        this.nome = nome;
    }
}

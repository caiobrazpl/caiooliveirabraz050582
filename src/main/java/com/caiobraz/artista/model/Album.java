package com.caiobraz.artista.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ALBUM")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "ATIVO")
    private Boolean ativo;

    @OneToMany(mappedBy = "album")
    private List<ArtistaAlbum> artistaAlbums;

    @Transient
    private List<String> fotos;

    public Album(Long id) {
        this.id = id;
    }

    public Album(String nome) {
        this.nome = nome;
    }
}

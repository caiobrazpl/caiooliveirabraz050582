package com.caiobraz.artista.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ALBUM_FOTO")
public class AlbumFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ALBUM")
    private Album album;

    @Column(name = "HASH")
    private String hash;

    @Column(name = "BUCKET")
    private String bucket;

    @Column(name = "DATA_UPLOAD")
    private LocalDateTime dataUpload;
}

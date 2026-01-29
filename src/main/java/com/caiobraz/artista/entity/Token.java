package com.caiobraz.artista.entity;

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
@Table(name = "TOKEN")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "REVOGADO")
    private Boolean revogado;

    @Column(name = "EXPIRADO")
    private Boolean expirado;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;
}

package com.caiobraz.artista.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "REVOKED")
    private boolean revoked;

    @Column(name = "EXPIRED")
    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}

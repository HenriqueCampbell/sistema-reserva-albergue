package com.albergue.domain.model;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;


@Entity
@Getter 
@Setter 
@NoArgsConstructor 
public class Quarto {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;
    private Integer capacidade;
    private String descricao;

    @Column(nullable = false)
    private boolean banheiro;
}

package com.albergue.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Entity
@Getter 
@Setter 
@NoArgsConstructor 
public class Leito {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String descricao;

    @ManyToOne 
    private Quarto quarto;

    @ManyToMany(mappedBy = "leitos")
    private List<Reserva> reservas = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
}

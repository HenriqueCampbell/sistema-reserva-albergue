package com.albergue.model;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter 
@Setter 
@NoArgsConstructor 
public class Cliente {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataPagamento;
    private BigDecimal valor;
    private Boolean status;
    
    @OneToOne 
    private Reserva reserva;
}

package com.albergue.model;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Reserva {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private BigDecimal valorTotal;
    
    @ManyToOne 
    private Quarto quarto;

    //@Enumerated(EnumType.STRING)
    //private StatusReserva status;

    //@ManyToOne
    //private Cliente cliente;

    @ManyToMany
    private List<Leito> leitos;
}

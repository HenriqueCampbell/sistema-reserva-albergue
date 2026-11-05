package com.albergue.domain.model;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

import com.albergue.domain.model.Reserva.StatusReserva;

@Entity
@Getter 
@Setter 
@NoArgsConstructor 
public class Reserva {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private TipoReserva tipoReserva;

    @Enumerated(EnumType.STRING)
    private StatusReserva status;
    
    @OneToOne 
    private Pagamento pagamento;
    
    @ManyToOne
    private Cliente cliente;

    @ManyToMany
    private List<Leito> leitos;

    public enum StatusReserva {
        PENDENTE,
        CONFIRMADA,
        CANCELADA
    }

    public enum TipoReserva {
        VAGA,
        QUARTO
    }
}

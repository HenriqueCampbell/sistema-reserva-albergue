package com.albergue.infraestruture.persistence.repository.Implementacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.albergue.domain.model.Cliente;
import com.albergue.domain.model.Leito;
import com.albergue.domain.model.Reserva;
import com.albergue.domain.repository.ReservaRepository;
import com.albergue.infraestruture.persistence.repository.jpa.ReservaJpaRepository;
import com.albergue.infraestruture.persistence.repository.jpa.ClienteJpaRepository;
import com.albergue.infraestruture.persistence.repository.jpa.LeitoJpaRepository;

@Repository
public class ReservaRepositoryImpl implements ReservaRepository {
   private final ReservaJpaRepository jpaRepository;
    private final LeitoJpaRepository leitoJpaRepository;
    private final ClienteJpaRepository clienteJpaRepository;

    public ReservaRepositoryImpl(ReservaJpaRepository jpaRepository, LeitoJpaRepository leitoJpaRepository, ClienteJpaRepository clienteJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.leitoJpaRepository = leitoJpaRepository;
        this.clienteJpaRepository = clienteJpaRepository;
    }

    //funcionalidades em si
    @Override
    public Reserva salvar(Reserva reserva) {
        return jpaRepository.save(reserva);
    }

    @Override
    public Optional<Reserva> buscarPorId(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Reserva> listarTodos() {
        return jpaRepository.findAll();
    }

    public List<Leito> consultarDisponibilidade(LocalDate dataEntrada, LocalDate dataSaida) {
        List<Leito> todosLeitos = leitoJpaRepository.findAll();
        List<Leito> disponiveis = new ArrayList<>();

        //Arrumar formatcao
        LocalDateTime entrada = dataEntrada.atTime(12, 0);
        LocalDateTime saida = dataSaida.atTime(12, 0);

        for (Leito leito : todosLeitos) {
            boolean ocupado = false;

            for (Reserva reserva : leito.getReservas()) {
                if (reserva.getStatus() != Reserva.StatusReserva.CANCELADA && reserva.getDataEntrada().isBefore(saida) && reserva.getDataSaida().isAfter(entrada)) {
                    ocupado = true;
                }
            }

            if (!ocupado) {
                disponiveis.add(leito);
            }
        }
        
        return disponiveis;
    }   

    @Override
    public List<Reserva> listarReservasPorCliente(Long clienteId) {
        return jpaRepository.findByClienteId(clienteId);
    }
}
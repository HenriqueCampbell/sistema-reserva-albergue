package com.albergue.application.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import com.albergue.domain.model.Cliente;
import com.albergue.domain.model.Leito;
import com.albergue.domain.model.Reserva;
import com.albergue.domain.repository.ClienteRepository;
import com.albergue.domain.repository.ReservaRepository;

@Service
public class ReservaService {
    private final ClienteRepository clienteRepository;
    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository, ClienteRepository clienteRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
    }

    public Reserva cadastrarReserva(Reserva reserva) {
        return reservaRepository.salvar(reserva );
    }

    public Reserva buscarReservaPorId(Long id) {
        return reservaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada com o ID: " + id));
    }

    public List<Reserva> listarTodasReservas() {
        return reservaRepository.listarTodos();
    }

    public List<Leito> consultarDisponibilidade(LocalDate dataEntrada, LocalDate dataSaida) {
        return reservaRepository.consultarDisponibilidade(dataEntrada, dataSaida);
    }

    public Reserva cadastrarReserva(Long clienteId, LocalDate dataEntrada, LocalDate dataSaida, List<Long> leitosIds) {
        Optional<Cliente> cliente = clienteRepository.buscarPorId(clienteId);
        List<Leito> existemLeitos = consultarDisponibilidade(dataEntrada, dataSaida);
        List<Leito> leitosEscolhidos = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (Long leitoId : leitosIds) {
            boolean disponivel = false;

            for (Leito leito : existemLeitos) {
                if (leito.getId().equals(leitoId)) {
                    disponivel = true;

                    long diarias = dataSaida.toEpochDay() - dataEntrada.toEpochDay();
                    valorTotal = valorTotal.add(leito.getValor().multiply(BigDecimal.valueOf(diarias)));
                    leitosEscolhidos.add(leito);
                }
            }

            if (!disponivel) {
                throw new RuntimeException("Leito não disponível: " + leitoId);
            }
        }

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente.orElseThrow(
            () -> new RuntimeException("Cliente não encontrado com o ID: " + clienteId)
        ));
        reserva.setDataEntrada(dataEntrada.atTime(12, 0));
        reserva.setDataSaida(dataSaida.atTime(12, 0));
        reserva.setStatus(Reserva.StatusReserva.PENDENTE);
        reserva.setLeitos(leitosEscolhidos);
        reserva.setValorTotal(valorTotal);

        return reservaRepository.salvar(reserva);
    }

    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.buscarPorId(reservaId)
            .orElseThrow(() -> new RuntimeException("Reserva não encontrada com o ID: " + reservaId));

            //Excecoes q impedema  reserva
        if (reserva.getStatus() == Reserva.StatusReserva.CANCELADA) {
            throw new RuntimeException("Reserva já está cancelada.");
        }

        if (reserva.getDataEntrada().toLocalDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Não é possível cancelar uma reserva que já começou.");
        }

        LocalDate limiteCancelamento = reserva.getDataEntrada().toLocalDate().minusDays(3);

        if (LocalDate.now().isAfter(limiteCancelamento)) {
            throw new RuntimeException("Prazo para cancelamento encerrado.");
        }

        reserva.setStatus(Reserva.StatusReserva.CANCELADA);
        reservaRepository.salvar(reserva);
    }

    public List<Reserva> listarReservasPorCliente(Long clienteId) {
        clienteRepository.buscarPorId(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + clienteId));

        return reservaRepository.listarReservasPorCliente(clienteId);
    }
}
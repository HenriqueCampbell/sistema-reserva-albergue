package com.albergue.application.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.albergue.domain.model.Cliente;
import com.albergue.domain.model.Leito;
import com.albergue.domain.model.Reserva;
import com.albergue.domain.repository.ClienteRepository;
import com.albergue.domain.repository.LeitoRepository;
import com.albergue.domain.repository.ReservaRepository;

@Service
public class ReservaService {
    private final ClienteRepository clienteRepository;
    private final LeitoRepository leitoRepository;
    private final ReservaRepository reservaRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            ClienteRepository clienteRepository,
            LeitoRepository leitoRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.leitoRepository = leitoRepository;
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
        return cadastrarReserva(clienteId, dataEntrada, dataSaida, Reserva.TipoReserva.VAGA, leitosIds);
    }

    public Reserva cadastrarReserva(
            Long clienteId,
            LocalDate dataEntrada,
            LocalDate dataSaida,
            Reserva.TipoReserva tipoReserva,
            List<Long> itensIds) {
        if (dataEntrada == null || dataSaida == null || !dataSaida.isAfter(dataEntrada)) {
            throw new IllegalArgumentException("A data de saída deve ser posterior à data de entrada.");
        }
        if (dataEntrada.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de entrada não pode estar no passado.");
        }
        if (tipoReserva == null || itensIds == null || itensIds.isEmpty()) {
            throw new IllegalArgumentException("Informe o tipo de reserva e pelo menos uma vaga ou quarto.");
        }
        if (itensIds.stream().distinct().count() != itensIds.size()) {
            throw new IllegalArgumentException("A seleção contém itens duplicados.");
        }

        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + clienteId));
        List<Leito> disponiveis = consultarDisponibilidade(dataEntrada, dataSaida);
        List<Leito> leitosEscolhidos;

        if (tipoReserva == Reserva.TipoReserva.VAGA) {
            leitosEscolhidos = selecionarVagasDisponiveis(itensIds, disponiveis);
        } else {
            leitosEscolhidos = selecionarQuartosDisponiveis(itensIds, disponiveis);
        }

        BigDecimal valorTotal = BigDecimal.ZERO;
        long diarias = dataSaida.toEpochDay() - dataEntrada.toEpochDay();
        for (Leito leito : leitosEscolhidos) {
            valorTotal = valorTotal.add(leito.getValor().multiply(BigDecimal.valueOf(diarias)));
        }

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setDataEntrada(dataEntrada.atTime(12, 0));
        reserva.setDataSaida(dataSaida.atTime(12, 0));
        reserva.setTipoReserva(tipoReserva);
        reserva.setStatus(Reserva.StatusReserva.PENDENTE);
        reserva.setLeitos(leitosEscolhidos);
        reserva.setValorTotal(valorTotal);

        return reservaRepository.salvar(reserva);
    }

    private List<Leito> selecionarVagasDisponiveis(List<Long> leitosIds, List<Leito> disponiveis) {
        var disponiveisPorId = disponiveis.stream()
                .collect(Collectors.toMap(Leito::getId, leito -> leito));
        List<Leito> selecionados = new ArrayList<>();
        for (Long leitoId : leitosIds) {
            Leito leito = disponiveisPorId.get(leitoId);
            if (leito == null) {
                throw new IllegalStateException("A vaga " + leitoId + " não está disponível para o período.");
            }
            selecionados.add(leito);
        }
        return selecionados;
    }

    private List<Leito> selecionarQuartosDisponiveis(List<Long> quartosIds, List<Leito> disponiveis) {
        var todosLeitos = leitoRepository.listarTodos();
        var disponiveisPorId = disponiveis.stream()
                .collect(Collectors.toMap(Leito::getId, leito -> leito));
        List<Leito> selecionados = new ArrayList<>();

        for (Long quartoId : quartosIds) {
            List<Leito> leitosDoQuarto = todosLeitos.stream()
                    .filter(leito -> leito.getQuarto() != null
                            && leito.getQuarto().getId().equals(quartoId))
                    .toList();
            if (leitosDoQuarto.isEmpty()) {
                throw new IllegalArgumentException("Quarto não encontrado: " + quartoId);
            }
            Integer capacidade = leitosDoQuarto.get(0).getQuarto().getCapacidade();
            if (capacidade == null || leitosDoQuarto.size() != capacidade) {
                throw new IllegalStateException("O cadastro de vagas do quarto " + quartoId
                        + " não corresponde à capacidade informada.");
            }
            if (leitosDoQuarto.stream().anyMatch(leito -> !disponiveisPorId.containsKey(leito.getId()))) {
                throw new IllegalStateException("Nem todas as vagas do quarto "
                        + leitosDoQuarto.get(0).getQuarto().getNumero()
                        + " estão disponíveis para o período.");
            }
            selecionados.addAll(leitosDoQuarto);
        }

        return selecionados;
    }

    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.buscarPorId(reservaId)
            .orElseThrow(() -> new RuntimeException("Reserva não encontrada com o ID: " + reservaId));

        if (reserva.getStatus() == Reserva.StatusReserva.CANCELADA) {
            throw new IllegalStateException("Esta reserva já está cancelada.");
        }

        if (reserva.getDataEntrada().toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Não é possível cancelar uma reserva que já começou.");
        }

        LocalDate limiteCancelamento = reserva.getDataEntrada().toLocalDate().minusDays(3);

        if (LocalDate.now().isAfter(limiteCancelamento)) {
            throw new IllegalStateException("O cancelamento deve ser feito com pelo menos 3 dias de antecedência.");
        }

        reserva.setStatus(Reserva.StatusReserva.CANCELADA);
        reservaRepository.salvar(reserva);
    }

    public Reserva confirmarPagamentoSimulado(Long reservaId) {
        Reserva reserva = buscarReservaPorId(reservaId);

        if (reserva.getStatus() == Reserva.StatusReserva.CANCELADA) {
            throw new IllegalStateException("Não é possível confirmar uma reserva cancelada.");
        }

        if (reserva.getStatus() == Reserva.StatusReserva.PENDENTE) {
            reserva.setStatus(Reserva.StatusReserva.CONFIRMADA);
            return reservaRepository.salvar(reserva);
        }

        return reserva;
    }

    public List<Reserva> listarReservasPorCliente(Long clienteId) {
        clienteRepository.buscarPorId(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + clienteId));

        return reservaRepository.listarReservasPorCliente(clienteId);
    }
}
package com.albergue.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.albergue.domain.model.Reserva;
import com.albergue.domain.model.Leito;

public interface ReservaRepository {
    //funcionalidades existentes("menu")
    Reserva salvar(Reserva reserva);
    Optional<Reserva> buscarPorId(Long id);
    List<Reserva> listarTodos();
    List<Leito> consultarDisponibilidade(LocalDate dataEntrada, LocalDate dataSaida);
    List<Reserva> listarReservasPorCliente(Long clienteId);
}
package com.albergue.presentation.controller;

import com.albergue.application.service.ReservaService;
import com.albergue.domain.model.Reserva;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@Valid @RequestBody CriarReservaRequest request) {
        Reserva reserva = reservaService.cadastrarReserva(
                request.clienteId(),
                request.dataEntrada(),
                request.dataSaida(),
                request.leitosIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodasReservas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarReservaPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Reserva>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.listarReservasPorCliente(clienteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }

    public record CriarReservaRequest(
            @NotNull Long clienteId,
            @NotNull LocalDate dataEntrada,
            @NotNull LocalDate dataSaida,
            @NotEmpty List<@NotNull Long> leitosIds
    ) {
    }
}
